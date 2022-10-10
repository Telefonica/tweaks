package com.telefonica.tweaks.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.telefonica.tweaks.domain.DropDownMenuTweakEntry
import com.telefonica.tweaks.domain.Editable
import com.telefonica.tweaks.domain.EditableBooleanTweakEntry
import com.telefonica.tweaks.domain.EditableIntTweakEntry
import com.telefonica.tweaks.domain.EditableLongTweakEntry
import com.telefonica.tweaks.domain.EditableStringTweakEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


interface TweaksRepository {
    fun isOverriden(entry: Editable<*>): Flow<Boolean?>
    fun <T> get(entry: Editable<T>): Flow<T?>

    suspend fun clearValue(entry: Editable<*>)

    suspend fun <T> setValue(entry: Editable<T>, value: T?)
}

class TweaksRepositoryImpl @Inject constructor(
    private val tweaksDataStore: TweaksDataStore,
) : TweaksRepository {

    override fun isOverriden(entry: Editable<*>): Flow<Boolean?> = tweaksDataStore.data
        .map { preferences -> preferences[buildIsOverridenKey(entry)] }

    override fun <T> get(entry: Editable<T>): Flow<T?> = tweaksDataStore.data
        .map { preferences -> preferences[buildKey(entry)] }

    override suspend fun clearValue(entry: Editable<*>) {
        tweaksDataStore.edit {
            it.remove(buildKey(entry))
            it.remove(buildIsOverridenKey(entry))
        }
    }

    override suspend fun <T> setValue(entry: Editable<T>, value: T?) {
        val key = buildKey(entry)
        val overridenKey = buildIsOverridenKey(entry)
        tweaksDataStore.edit { preferences ->
            if (value != null) {
                preferences[key] = value
                preferences[overridenKey] = true
            } else {
                preferences.remove(key)
                preferences[overridenKey] = false
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> buildKey(entry: Editable<T>): Preferences.Key<T> = when (entry) {
        is EditableStringTweakEntry -> stringPreferencesKey(entry.key) as Preferences.Key<T>
        is EditableBooleanTweakEntry -> booleanPreferencesKey(entry.key) as Preferences.Key<T>
        is EditableIntTweakEntry -> intPreferencesKey(entry.key) as Preferences.Key<T>
        is EditableLongTweakEntry -> longPreferencesKey(entry.key) as Preferences.Key<T>
        is DropDownMenuTweakEntry -> stringPreferencesKey(entry.key) as Preferences.Key<T>
        else -> throw java.lang.IllegalStateException("Unknown tweak entry")
    }

    private fun buildIsOverridenKey(entry: Editable<*>): Preferences.Key<Boolean> =
        booleanPreferencesKey("${entry.key}.TweakOverriden")
}