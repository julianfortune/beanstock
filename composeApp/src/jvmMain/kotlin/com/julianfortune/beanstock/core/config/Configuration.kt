package com.julianfortune.beanstock.core.config

import com.julianfortune.beanstock.core.system.Resources
import tools.jackson.databind.ObjectMapper

data class Configuration(
    val windowTitle: String,
    val db: Db,
) {
    data class Db(val location: FileLocation)

    companion object {
        fun getFileName(environment: Environment): String {
            return when (environment) {
                Environment.DEVELOPMENT -> "configuration-dev.yaml"
                Environment.RELEASE -> "configuration-release.yaml"
            }
        }

        fun load(mapper: ObjectMapper, environment: Environment): Configuration {
            val fileName = getFileName(environment)
            return Resources.load(mapper, fileName)
        }
    }
}
