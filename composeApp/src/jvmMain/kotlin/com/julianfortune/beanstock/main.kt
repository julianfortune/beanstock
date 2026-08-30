package com.julianfortune.beanstock

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.julianfortune.beanstock.core.config.Configuration
import com.julianfortune.beanstock.core.config.Environment
import com.julianfortune.beanstock.core.config.FileLocation
import com.julianfortune.beanstock.core.system.AppDataManager
import com.julianfortune.beanstock.core.system.Platform
import com.julianfortune.beanstock.db.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.nio.file.Paths

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
fun main() {
    // Makes app bar match system theme on macOS
    System.setProperty("apple.awt.application.appearance", "system")

    val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    val environment = Environment.fromSystem(Environment.RELEASE)
    val configuration = Configuration.load(mapper, environment)

    val adm = AppDataManager(Platform.current)

    val databaseDirectory = when (configuration.db.location) {
        FileLocation.APP_DATA -> {
            adm.initialize()
            adm.appDataPath
        }
        FileLocation.WORKING_DIRECTORY -> Paths.get("")
    }

    // Debug output
    println("osName=${Platform.osName}")
    println("environment=$environment")
    println("configuration=$configuration")

    val driver = runBlocking {
        DatabaseDriverFactory(databaseDirectory).createDriver()
    }

    startKoin {
        modules(
            appModule,
            module { single { driver } }
        )
    }

    application {
        // Customize the initial state of the window
        val windowState = rememberWindowState(
            initialBoundsProvider = WindowBoundsProvider(
                sizeProvider = WindowSizeProvider.Fixed(size = DpSize(1200.dp, 700.dp))
            )
        )

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = configuration.windowTitle,
            minSize = DpSize(800.dp, 600.dp),
        ) {
            App()
        }
    }
}
