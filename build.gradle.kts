plugins {
    // this is necessary to avoid the plugins being loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.sqldelight) apply false
}
