package com.julianfortune.beanstock.core.system

enum class Platform {
    LINUX,
    MACOS,
    WINDOWS,
    ;

    companion object {
        val osName: String = System.getProperty("os.name") ?: "unknown"
        private val normalizedOsName = osName.lowercase()

        val current = when {
            normalizedOsName.contains("win") -> WINDOWS
            normalizedOsName.contains("mac") || normalizedOsName.contains("darwin") -> MACOS
            else -> LINUX
        }
    }
}