package com.telefonica.tweaks.domain

import androidx.datastore.preferences.core.*
import com.telefonica.tweaks.data.TweaksDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class TweaksBusinessLogic @Inject constructor(
    private val tweaksDataStore: TweaksDataStore,
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
        val editableCasted = entry
        val defaultValue: Flow<T> = editableCasted.defaultValue

        return isOverriden(entry)
            .flatMapMerge { overriden ->
                when (overriden) {
                    true -> getFromStorage(entry)
                    else -> defaultValue
                }
            }
    }

    private fun isOverriden(entry: Editable<*>): Flow<Boolean> = tweaksDataStore.data
        .map { preferences -> preferences[buildIsOverridenKey(entry)] ?: OVERRIDEN_DEFAULT_VALUE }

    private fun <T> getFromStorage(entry: Editable<T>) =
        tweaksDataStore.data
            .map { preferences -> preferences[buildKey(entry)] }

    internal suspend fun <T> clearValue(entry: Editable<T>) {
        tweaksDataStore.edit {
            it.remove(buildKey(entry))
            it.remove(buildIsOverridenKey(entry))
        }
    }

    suspend fun <T> setValue(entry: Editable<T>, value: T?) {
        tweaksDataStore.edit {
            if (value != null) {
                it[buildKey(entry)] = value
                it[buildIsOverridenKey(entry)] = true
            } else {
                it.remove(buildKey(entry))
                it[buildIsOverridenKey(entry)] = false
            }
        }
    }

    suspend fun <T> setValue(key: String, value: T?) {
        val tweakEntry = keyToEntryValueMap[key] as Editable<T>
        setValue(tweakEntry, value)
    }

    suspend fun <T> clearValue(key: String) {
        val tweakEntry = keyToEntryValueMap[key] as Editable<T>
        clearValue(tweakEntry)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> buildKey(entry: Editable<T>): Preferences.Key<T> = when (entry) {
//        is ReadOnlyStringTweakEntry -> stringPreferencesKey(entry.key) as Preferences.Key<T>
        is EditableStringTweakEntry -> stringPreferencesKey(entry.key) as Preferences.Key<T>
        is EditableBooleanTweakEntry -> booleanPreferencesKey(entry.key) as Preferences.Key<T>
        is EditableIntTweakEntry -> intPreferencesKey(entry.key) as Preferences.Key<T>
        is EditableLongTweakEntry -> longPreferencesKey(entry.key) as Preferences.Key<T>
        is DropDownMenuTweakEntry -> stringPreferencesKey(entry.key) as Preferences.Key<T>
//        is ButtonTweakEntry -> throw java.lang.IllegalStateException("Buttons doesn't have keys")
//        is RouteButtonTweakEntry -> throw java.lang.IllegalStateException("Buttons doesn't have keys")
//        is CustomNavigationButtonTweakEntry -> throw java.lang.IllegalStateException("Buttons doesn't have keys")
        else -> throw java.lang.IllegalStateException("Unknown tweak entry")
    }

    private fun buildIsOverridenKey(entry: Editable<*>): Preferences.Key<Boolean> =
        booleanPreferencesKey("${entry.key}.TweakOverriden")

    companion object {
        private const val OVERRIDEN_DEFAULT_VALUE = false
    }
}