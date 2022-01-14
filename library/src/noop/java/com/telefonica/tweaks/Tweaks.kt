package com.telefonica.tweaks

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.telefonica.tweaks.domain.*
import kotlinx.coroutines.flow.Flow

open class Tweaks {

    private val keyToEntryValueMap: MutableMap<String, TweakEntry<*>> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    open fun <T> getTweakValue(key: String): Flow<T?> {
        val entry= keyToEntryValueMap[key] as TweakEntry<T>
        return getTweakValue(entry)
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T> getTweakValue(entry: TweakEntry<T>): Flow<T?> = when (entry as Modifiable) {
        is ReadOnly<*> -> (entry as ReadOnly<T>).value
        is Editable<*> -> (entry as Editable<T>).defaultValue
    }

    open suspend fun <T> setTweakValue(key: String, value: T?) {
    }

    open suspend fun <T> setTweakValue(entry: TweakEntry<T>, value: T?) {
    }

    open suspend fun <T> clearValue(entry: TweakEntry<T>) {
    }

    open suspend fun <T> clearValue(key: String) {
    }

    private fun initialize(tweaksGraph: TweaksGraph) {
        val allEntries: List<TweakEntry<*>> = tweaksGraph.categories
            .flatMap { category ->
                category.groups.flatMap { group ->
                    group.entries
                }
            }
        allEntries.forEach { entry ->
            keyToEntryValueMap[entry.key] = entry
        }
    }

    companion object {
        const val TWEAKS_NAVIGATION_ENTRYPOINT = "tweaks"
        private var reference: Tweaks = Tweaks()

        @JvmStatic
        fun init(
            application: Application,
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
    customComposableScreens: NavGraphBuilder.() -> Unit = {}
) {}

@Composable
fun NavController.navigateToTweaksOnShake() {}
