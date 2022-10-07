package com.telefonica.tweaks.demo.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DebugTweaksTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(colors = ColorPalette, typography = TweaksTypography, content = content)
}