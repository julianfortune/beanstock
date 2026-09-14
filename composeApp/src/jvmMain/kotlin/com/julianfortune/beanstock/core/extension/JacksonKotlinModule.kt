package com.julianfortune.beanstock.core.extension

import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule

fun jacksonYamlMapper() = YAMLMapper.builder().addModule(KotlinModule.Builder().build()).build()
