package com.telefonica.tweaks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.telefonica.tweaks.ui.theme.TweaksDarkBlue900
import com.telefonica.tweaks.ui.theme.TweaksGreen500

@Immutable
data class TweaksColors(
    val tweaksPrimary: Color,
    val tweaksPrimaryVariant: Color,
    val tweaksSurface: Color,
    val tweaksOnSufrace: Color,
    val tweaksBackground: Color,
    val tweaksOnBackground: Color,
)

val LocalTweaksColors = staticCompositionLocalOf {
    TweaksColors(
        tweaksPrimary = Color.Unspecified,
        tweaksPrimaryVariant = Color.Unspecified,
        tweaksSurface = Color.Unspecified,
        tweaksOnSufrace = Color.Unspecified,
        tweaksBackground = Color.Unspecified,
        tweaksOnBackground = Color.Unspecified,
    )
}

@Composable
fun TweaksTheme(
    content: @Composable () -> Unit,
) {
    val tweaksColors = TweaksColors(
        tweaksPrimary = TweaksGreen500,
        tweaksPrimaryVariant = TweaksGreen500,
        tweaksSurface = TweaksDarkBlue900,
        tweaksOnSufrace = Color.White,
        tweaksBackground = TweaksDarkBlue900,
        tweaksOnBackground = Color.White
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