package com.telefonica.tweaks

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.telefonica.tweaks.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

open class Tweaks : TweaksContract {

    private val keyToEntryValueMap: MutableMap<String, Editable<*>> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getTweakValue(key: String): Flow<T?> {
        val entry= keyToEntryValueMap[key] as TweakEntry
        return getTweakValue(entry)
    }

    override fun <T> getTweakValue(key: String, defaultValue: T): Flow<T> =
        getTweakValue<T>(key).map { it ?: defaultValue }

    override suspend fun <T> getTweak(key: String): T? =
        getTweakValue<T>(key).firstOrNull()

    override suspend fun <T> getTweak(key: String, defaultValue: T): T =
        getTweak(key) ?: defaultValue

    @Suppress("UNCHECKED_CAST")
    private fun <T> getTweakValue(entry: TweakEntry): Flow<T?> = when (entry) {
        is ReadOnly<*> -> (entry as ReadOnly<T>).value
        is Editable<*> -> (entry as Editable<T>).defaultValue ?: flowOf()
        else -> flowOf()
    }

    override suspend fun <T> setTweakValue(key: String, value: T) {}

    override suspend fun clearValue(key: String) {}

    private fun initialize(tweaksGraph: TweaksGraph) {
        val allEntries: List<Editable<*>> = tweaksGraph.categories
            .flatMap { category ->
                category.groups.flatMap { group ->
                    group.entries
                }
            }.filterIsInstance<Editable<*>>()
        allEntries.forEach { entry ->
            keyToEntryValueMap[entry.key] = entry
        }
    }

    companion object {
        const val TWEAKS_NAVIGATION_ENTRYPOINT = "tweaks"
        private var reference: Tweaks = Tweaks()

        @JvmStatic
        fun init(
            context: Context,
            tweaksGraph: TweaksGraph,
        ) {
            reference.initialize(tweaksGraph)
        }

        @JvmStatic
        fun getReference(): Tweaks = reference
    }


}

fun NavGraphBuilder.addTweakGraph(
    navController: NavController,
    tweaksCustomTheme: @Composable (block: @Composable () -> Unit) -> Unit = { it() },
    customComposableScreens: NavGraphBuilder.() -> Unit = {},
) {}

@Composable
fun NavController.navigateToTweaksOnShake() {}
