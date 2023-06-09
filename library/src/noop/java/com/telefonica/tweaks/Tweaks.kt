package com.telefonica.tweaks

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.telefonica.tweaks.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class Tweaks {

    private val keyToEntryValueMap: MutableMap<String, Editable<*>> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    open fun <T> getTweakValue(key: String): Flow<T?> {
        val entry= keyToEntryValueMap[key] as TweakEntry
        return getTweakValue(entry)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getTweakValue(entry: TweakEntry): Flow<T?> = when (entry) {
        is ReadOnly<*> -> (entry as ReadOnly<T>).value
        is Editable<*> -> (entry as Editable<T>).defaultValue ?: flowOf()
        else -> flowOf()
    }

    open suspend fun <T> setTweakValue(key: String, value: T) {}

    open suspend fun clearValue(key: String) {}

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
