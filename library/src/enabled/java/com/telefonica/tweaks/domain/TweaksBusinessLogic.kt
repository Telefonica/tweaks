package com.telefonica.tweaks.domain

import com.telefonica.tweaks.data.TweaksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class TweaksBusinessLogic @Inject constructor(
    private val tweaksRepository: TweaksRepository
) {

    internal lateinit var tweaksGraph: TweaksGraph
    private val keyToEntryValueMap: MutableMap<String, Editable<*>> = mutableMapOf()

    internal fun initialize(tweaksGraph: TweaksGraph) {
        this.tweaksGraph = tweaksGraph
        val alreadyIntroducedKeys = mutableSetOf<String>()
        val allEntries: MutableList<TweakEntry> = tweaksGraph.categories
            .flatMap { category ->
                category.groups.flatMap { group ->
                    group.entries
                }
            }.toMutableList()
        if (tweaksGraph.cover != null) {
            allEntries.addAll(tweaksGraph.cover.entries)
        }

        allEntries
            .filterIsInstance<Editable<*>>()
            .forEach { entry ->
                checkIfRepeatedKey(alreadyIntroducedKeys, entry)
                keyToEntryValueMap[entry.key] = entry
            }
    }

    private fun checkIfRepeatedKey(
        alreadyIntroducedKeys: MutableSet<String>,
        entry: Editable<*>,
    ) {
        if (alreadyIntroducedKeys.contains(entry.key)) {
            throw IllegalStateException("There is a repeated key in the tweaks: ${entry.key}, review your graph")
        }

        alreadyIntroducedKeys.add(entry.key)
    }

    fun <T> getValue(key: String): Flow<T?> {
        val tweakEntry = keyToEntryValueMap[key] as TweakEntry
        return getValue(tweakEntry)
    }

    internal fun <T> getValue(entry: TweakEntry): Flow<T?> = when (entry) {
        is ReadOnly<*> -> (entry as ReadOnly<T>).value
        is Editable<*> -> getMutableValue(entry as Editable<T>)
        else -> emptyFlow()
    }

    private fun <T> getMutableValue(entry: Editable<T>): Flow<T?> {
        val defaultValue: Flow<T> = entry.defaultValue ?: flowOf()

        return isOverridden(entry)
            .flatMapMerge { overriden ->
                when (overriden) {
                    true -> getFromStorage(entry)
                    else -> defaultValue
                }
            }
    }

    fun isOverridden(entry: Editable<*>): Flow<Boolean> =
        tweaksRepository.isOverriden(entry).map { it ?: OVERRIDEN_DEFAULT_VALUE }

    private fun <T> getFromStorage(entry: Editable<T>) = tweaksRepository.get(entry)

    internal suspend fun clearValue(entry: Editable<*>) {
        tweaksRepository.clearValue(entry)
    }

    suspend fun <T> setValue(entry: Editable<T>, value: T?) {
        tweaksRepository.setValue(entry, value)
    }

    suspend fun <T> setValue(key: String, value: T?) {
        val tweakEntry = keyToEntryValueMap[key] as Editable<T>
        setValue(tweakEntry, value)
    }

    suspend fun clearValue(key: String) {
        val tweakEntry = keyToEntryValueMap[key] as Editable<*>
        clearValue(tweakEntry)
    }

    companion object {
        private const val OVERRIDEN_DEFAULT_VALUE = false
    }
}