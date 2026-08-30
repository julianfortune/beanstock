package com.julianfortune.beanstock.core.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.julianfortune.beanstock.core.system.Resources

data class Configuration(
    val windowTitle: String,
    val db: Db,
    val feature: Features
) {
    data class Db(val location: FileLocation)

    data class Features(val reportDebug: FeatureFlag)

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
