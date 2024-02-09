package com.telefonica.tweaks

import kotlinx.coroutines.flow.Flow


interface TweaksContract {

    fun <T> getTweakValue(key: String): Flow<T?>

    fun <T> getTweakValue(key: String, defaultValue: T): Flow<T>

    suspend fun <T> getTweak(key: String): T?

    suspend fun <T> getTweak(key: String, defaultValue: T): T

    suspend fun <T> setTweakValue(key: String, value: T)

    suspend fun clearValue(key: String)


}