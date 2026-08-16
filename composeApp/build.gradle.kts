import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.sqldelight)
    id("java-test-fixtures")
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
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)
            implementation(compose.ui)

            // Forkhandles
            implementation(project.dependencies.platform(libs.forkhandles.bom))
            implementation("dev.forkhandles:result4k")

            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
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
        mainClass = "com.julianfortune.glacier.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Glacier"
            packageVersion = "0.0.4"

            modules("java.sql")

            // TODO ..
//            vendor = "TODO"
//            description = "TODO"
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
            packageName = "com.julianfortune.glacier.db"
            generateAsync = true
        }
    }
}
