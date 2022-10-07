package com.telefonica.tweaks.domain

import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun tweaksGraph(block: TweaksGraph.Builder.() -> Unit): TweaksGraph {
    val builder = TweaksGraph.Builder()
    builder.block()
    return builder.build()
}

/** The top level node of the tweak graphs. It contains a list of categories (screens)*/
data class TweaksGraph(val cover: TweakGroup?, val categories: List<TweakCategory>) {
    class Builder {
        private val categories = mutableListOf<TweakCategory>()
        private var cover: TweakGroup? = null

        fun cover(title: String, block: TweakGroup.Builder.() -> Unit) {
            val builder = TweakGroup.Builder(title)
            builder.block()
            cover = builder.build()
        }

        fun category(title: String, block: TweakCategory.Builder.() -> Unit) {
            val builder = TweakCategory.Builder(title)
            builder.block()
            categories.add(builder.build())
        }

        internal fun build(): TweaksGraph = TweaksGraph(cover, categories)
    }
}

/** A tweak category is a screen, for example your app tweaks can be splitted by features
 *  (chat, video, login...) each one of those can be a category
 *  */
data class TweakCategory(val title: String, val groups: List<TweakGroup>) {
    class Builder(private val title: String) {
        private val groups = mutableListOf<TweakGroup>()

        fun group(title: String, block: TweakGroup.Builder.() -> Unit) {
            val builder = TweakGroup.Builder(title)
            builder.block()
            groups.add(builder.build())
        }

        internal fun build(): TweakCategory = TweakCategory(title, groups)
    }
}

/** A bunch of tweaks that are related to each other, for example: domain & port for the backend server configurations*/
data class TweakGroup(val title: String, val entries: List<TweakEntry>) {
    class Builder(private val title: String) {
        private val entries = mutableListOf<TweakEntry>()

        fun tweak(entry: TweakEntry) {
            entries.add(entry)
        }

        fun button(
            key: String,
            name: String,
            action: () -> Unit,
        ) {
            tweak(ButtonTweakEntry(name, action))
        }

        fun routeButton(
            name: String,
            route: String,
        ) {
            tweak(RouteButtonTweakEntry(name, route))
        }

        fun customNavigationButton(
            name: String,
            navigation: (NavController) -> Unit,
        ) {
            tweak(CustomNavigationButtonTweakEntry(name, navigation))
        }

        fun label(
            name: String,
            value: () -> Flow<String>,
        ) {
            tweak(ReadOnlyStringTweakEntry(name, value()))
        }

        fun editableString(
            key: String,
            name: String,
            defaultValue: Flow<String>? = null,
        ) {
            tweak(EditableStringTweakEntry(key, name, defaultValue))
        }

        fun editableString(
            key: String,
            name: String,
            defaultValue: String,
        ) {
            tweak(EditableStringTweakEntry(key, name, defaultValue))
        }

        fun editableBoolean(
            key: String,
            name: String,
            defaultValue: Flow<Boolean>? = null,
        ) {
            tweak(EditableBooleanTweakEntry(key, name, defaultValue))
        }

        fun editableBoolean(
            key: String,
            name: String,
            defaultValue: Boolean,
        ) {
            tweak(EditableBooleanTweakEntry(key, name, defaultValue))
        }

        fun editableInt(
            key: String,
            name: String,
            defaultValue: Flow<Int>? = null,
        ) {
            tweak(EditableIntTweakEntry(key, name, defaultValue))
        }

        fun editableInt(
            key: String,
            name: String,
            defaultValue: Int,
        ) {
            tweak(EditableIntTweakEntry(key, name, defaultValue))
        }

        fun editableLong(
            key: String,
            name: String,
            defaultValue: Flow<Long>? = null,
        ) {
            tweak(EditableLongTweakEntry(key, name, defaultValue))
        }

        fun editableLong(
            key: String,
            name: String,
            defaultValue: Long,
        ) {
            tweak(EditableLongTweakEntry(key, name, defaultValue))
        }

        fun dropDownMenu(
            key: String,
            name: String,
            values: List<String>,
            defaultValue: Flow<String>,
        ) {
            tweak(DropDownMenuTweakEntry(key, name, values, defaultValue))
        }

        internal fun build(): TweakGroup = TweakGroup(title, entries)
    }
}

sealed class TweakEntry(
    val name: String,
): Modifiable

sealed interface Modifiable
interface Editable<T> : Modifiable {
    val key: String
    val defaultValue: Flow<T>?
}
interface ReadOnly<T> : Modifiable {
    val value: Flow<T>
}

/** A button, with a customizable action*/
class ButtonTweakEntry(name: String, val action: () -> Unit) :
    TweakEntry(name = name)

/** A button, that when tapped navigates to a route*/
class RouteButtonTweakEntry(name: String, val route: String) :
    TweakEntry(name = name)

/**
 * A button, that when tapped will execute the navigation specified
 * using the NavController it receives as param
 */
class CustomNavigationButtonTweakEntry(
    name: String,
    val navigation: (NavController) -> Unit,
) : TweakEntry(name = name)

/** A non editable entry */
class ReadOnlyStringTweakEntry(
    name: String,
    override val value: Flow<String>,
) : TweakEntry(name), ReadOnly<String>

/** An editable entry. It can be modified by using long-press*/
class EditableStringTweakEntry(
    override val key: String,
    name: String,
    override val defaultValue: Flow<String>? = null,
) : TweakEntry(name = name), Editable<String> {
    constructor(
        key: String,
        name: String,
        defaultUniqueValue: String,
    ) : this(key, name, flowOf(defaultUniqueValue))
}

/** An editable entry. It can be modified by using long-press*/
class EditableBooleanTweakEntry(
    override val key: String,
    name: String,
    override val defaultValue: Flow<Boolean>? = null,
) : TweakEntry(name = name), Editable<Boolean> {
    constructor(
        key: String,
        name: String,
        defaultUniqueValue: Boolean,
    ) : this(key, name, flowOf(defaultUniqueValue))
}

/** An editable entry. It can be modified by using long-press*/
class EditableIntTweakEntry(
    override val key: String,
    name: String,
    override val defaultValue: Flow<Int>? = null,
) : TweakEntry(name), Editable<Int> {
    constructor(
        key: String,
        name: String,
        defaultUniqueValue: Int,
    ) : this(key, name, flowOf(defaultUniqueValue))
}

/** An editable entry. It can be modified by using long-press*/
class EditableLongTweakEntry(
    override val key: String,
    name: String,
    override val defaultValue: Flow<Long>? = null,
) : TweakEntry(name = name), Editable<Long> {
    constructor(
        key: String,
        name: String,
        defaultUniqueValue: Long,
    ) : this(key, name, flowOf(defaultUniqueValue))
}

class DropDownMenuTweakEntry(
    override val key: String,
    name: String,
    val values: List<String>,
    override val defaultValue: Flow<String>,
) : TweakEntry(name = name), Editable<String> {
    constructor(
        key: String,
        name: String,
        values: List<String>,
        defaultValue: String,
    ) : this(key, name, values, flowOf(defaultValue))
}

internal object Constants {
    const val TWEAK_MAIN_SCREEN = "tweaks-main-screen"
}