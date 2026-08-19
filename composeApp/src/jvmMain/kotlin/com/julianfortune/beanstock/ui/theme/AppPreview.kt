package com.julianfortune.beanstock.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppPreview(
    colorScheme: ColorScheme = darkScheme, //  darkScheme or lightScheme
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography(),
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}