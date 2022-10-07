package com.telefonica.tweaks.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color


val TweaksGreen500 = Color(0xFF1EB980)
val TweaksDarkBlue900 = Color(0xFF26282F)

// Rally is always dark themed.
val TweaksColorPalette = darkColors(
    primary = TweaksGreen500,
    primaryVariant = TweaksGreen500,
    surface = TweaksDarkBlue900,
    onSurface = Color.White,
    background = TweaksDarkBlue900,
    onBackground = Color.White
)
