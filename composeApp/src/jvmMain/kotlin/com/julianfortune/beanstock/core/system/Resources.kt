package com.julianfortune.beanstock.core.system

import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

object Resources {
    inline fun <reified T> load(mapper: ObjectMapper, fileName: String): T {
        val inputStream =
            this::class.java.classLoader.getResourceAsStream(fileName)
                ?: throw IllegalArgumentException("Resource with name '$fileName' could not be found.")

        return inputStream.use { mapper.readValue<T>(it) }
    }
}
