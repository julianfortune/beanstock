package com.julianfortune.beanstock.data.codec

sealed interface CodecError {
    data class InvalidInput(val input: String): CodecError
}