import com.diffplug.spotless.kotlin.KtfmtStep
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.spotless)
    alias(libs.plugins.sqldelight)
    id("java-test-fixtures")
}

// Formatting configuration
configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("src/*/kotlin/**/*.kt")

        ktfmt("0.64").kotlinlangStyle().configure {
            it.setMaxWidth(120)
            it.setTrailingCommaManagementStrategy(KtfmtStep.TrailingCommaManagementStrategy.COMPLETE)
        }
    }

    kotlinGradle {
        // targets "*.gradle.kts"
        ktfmt("0.64").kotlinlangStyle().configure {
            it.setMaxWidth(120)
            it.setTrailingCommaManagementStrategy(KtfmtStep.TrailingCommaManagementStrategy.COMPLETE)
        }
    }
}

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
                showStandardStreams = true
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)

            // Forkhandles
            implementation(project.dependencies.platform(libs.forkhandles.bom))
            implementation("dev.forkhandles:result4k")

            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core)
            implementation(libs.sqldelight.coroutines)
        }
        jvmMain.dependencies {
            implementation(libs.jackson.core)
            implementation(libs.jackson.yaml)
            implementation(libs.jackson.kotlin)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.sqldelight.driver)
        }
        jvmTest.dependencies {
            implementation(libs.assertj)
            implementation(libs.junit.jupiter)
            implementation(libs.mockk)
            implementation(project.dependencies.platform(libs.junit.bom))
            implementation(libs.kotlinx.coroutines.test)
            runtimeOnly(libs.junit.platform.launcher)
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

compose.desktop {
    application {
        mainClass = "com.julianfortune.beanstock.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Beanstock"
            packageVersion = "1.0.0"
            // TODO(?)
            // vendor = "..."
            // description = "..."

            modules("java.sql")

            // Configure the Windows installer to create a Start Menu entry for the app
            windows {
                menu = true
            }
        }

        // When running the application directly set the environment to 'development'
        // NOTE: Env flag doesn't work with `hotRun*` for some weird reason
        if (gradle.startParameter.taskNames.contains("run")) {
            jvmArgs += listOf("-Denv=DEVELOPMENT")
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            dialect(libs.sqldelight.sqlite.dialect)
            packageName = "com.julianfortune.beanstock.db"
            generateAsync = true
        }
    }
}
