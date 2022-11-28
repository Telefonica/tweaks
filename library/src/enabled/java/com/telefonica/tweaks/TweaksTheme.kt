package com.telefonica.tweaks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.telefonica.tweaks.ui.theme.TweaksDarkBlue
import com.telefonica.tweaks.ui.theme.TweaksDarkBlueBackground
import com.telefonica.tweaks.ui.theme.TweaksGreen

@Immutable
data class TweaksColors(
    val tweaksPrimary: Color,
    val tweaksOnPrimary: Color,
    val tweaksPrimaryVariant: Color,
    val tweaksSurface: Color,
    val tweaksOnSurface: Color,
    val tweaksBackground: Color,
    val tweaksOnBackground: Color,
    val tweaksGroupBackground: Color,
    val tweaksColorModified: Color,
)

val LocalTweaksColors = staticCompositionLocalOf {
    TweaksColors(
        tweaksPrimary = Color.Unspecified,
        tweaksOnPrimary = Color.Unspecified,
        tweaksPrimaryVariant = Color.Unspecified,
        tweaksSurface = Color.Unspecified,
        tweaksOnSurface = Color.Unspecified,
        tweaksBackground = Color.Unspecified,
        tweaksOnBackground = Color.Unspecified,
        tweaksGroupBackground = Color.Unspecified,
        tweaksColorModified = Color.Unspecified,
    )
}

@Composable
fun DefaultTweaksTheme(
    content: @Composable () -> Unit,
) {
    val tweaksColors = TweaksColors(
        tweaksPrimary = TweaksGreen,
        tweaksOnPrimary = Color.White,
        tweaksPrimaryVariant = TweaksGreen,
        tweaksSurface = TweaksDarkBlue,
        tweaksOnSurface = Color.White,
        tweaksBackground = TweaksDarkBlue,
        tweaksOnBackground = Color.White,
        tweaksGroupBackground = TweaksDarkBlueBackground,
        tweaksColorModified = TweaksDarkBlueBackground,
    )
    CompositionLocalProvider(LocalTweaksColors provides tweaksColors) {
        content()
    }
}

object TweaksTheme {
    val colors: TweaksColors
    @Composable
    get() = LocalTweaksColors.current
}