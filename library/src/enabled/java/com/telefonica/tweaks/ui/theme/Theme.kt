package com.telefonica.tweaks.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DebugTweaksTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(colors = TweaksColorPalette, typography = TweaksTypography, content = content)
}