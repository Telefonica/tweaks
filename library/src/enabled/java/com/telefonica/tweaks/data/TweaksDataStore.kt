package com.telefonica.tweaks.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject
import javax.inject.Singleton

internal val Context.tweaksDataStore: DataStore<Preferences> by preferencesDataStore(name = "debug_tweaks")

@Singleton
class TweaksDataStore @Inject constructor(
    context: Context,
): DataStore<Preferences> by context.tweaksDataStore
