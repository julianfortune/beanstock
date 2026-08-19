package com.julianfortune.beanstock.core.config

enum class Environment {
    DEVELOPMENT,
    RELEASE,
    ;

    companion object {
        val nameToEntry = entries.associateBy { it.name }

        fun fromSystem(fallback: Environment): Environment {
            return System.getProperty(CustomProperties.ENV)?.let { nameToEntry.getValue(it) } ?: fallback
        }
    }

}
