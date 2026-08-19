package com.julianfortune.beanstock.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import beanstock.composeapp.generated.resources.Res
import beanstock.composeapp.generated.resources.google_sans
import beanstock.composeapp.generated.resources.google_sans_italic
import org.jetbrains.compose.resources.Font


@Composable
fun googleSansFontFamily(): FontFamily {
    return FontFamily(
        Font(resource = Res.font.google_sans, style = FontStyle.Normal),
        Font(resource = Res.font.google_sans_italic, style = FontStyle.Italic),
    )
}


@Composable
fun AppTypography(): Typography {
    val fontFamily = googleSansFontFamily()

    val typography = Typography()
    return Typography(
        displayLarge = typography.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = typography.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = typography.displaySmall.copy(fontFamily = fontFamily),

        headlineLarge = typography.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = typography.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = typography.headlineSmall.copy(fontFamily = fontFamily),

        titleLarge = typography.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = typography.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = typography.titleSmall.copy(fontFamily = fontFamily),

        bodyLarge = typography.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = typography.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = typography.bodySmall.copy(fontFamily = fontFamily),

        labelLarge = typography.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = typography.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = typography.labelSmall.copy(fontFamily = fontFamily),
    )
}