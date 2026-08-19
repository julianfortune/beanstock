package com.julianfortune.beanstock.core.system

import com.julianfortune.beanstock.core.config.Constants
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class AppDataManager(platform: Platform) {

    companion object {
        // Path to the directory for application data relative to the user's home
        // Source: https://platformdirs.readthedocs.io/en/latest/platforms.html
        val relativePathByPlatform = mapOf(
            Platform.WINDOWS to "\\AppData\\Local\\", // Note: Need to prefix with `System.getProperty("user.home")`
            Platform.LINUX to "/.local/share/", //Note: Prefix with `~`
            Platform.MACOS to "/Library/Application Support/", // Note: Prefix with `~`
        )
    }

    private val userHome = System.getProperty("user.home")
    private val relativePath = relativePathByPlatform[platform]
    val appDataPath: Path = Paths.get(userHome, relativePath, Constants.APP_DATA_DIRECTORY_NAME)

    fun initialize() {
        if (!appDataPath.exists()) {
            appDataPath.createDirectory()
        }
    }
}