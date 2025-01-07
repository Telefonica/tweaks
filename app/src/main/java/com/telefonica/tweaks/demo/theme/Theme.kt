package com.telefonica.tweaks.demo.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.telefonica.tweaks.ui.theme.TweaksTypography

@Composable
fun DebugTweaksTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(colorScheme = TweaksColorPalette, typography = TweaksTypography, content = content)
}