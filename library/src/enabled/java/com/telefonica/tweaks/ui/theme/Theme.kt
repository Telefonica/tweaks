package com.telefonica.tweaks.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DebugTweaksTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(colors = TweaksColorPalette, typography = TweaksTypography, content = content)
}