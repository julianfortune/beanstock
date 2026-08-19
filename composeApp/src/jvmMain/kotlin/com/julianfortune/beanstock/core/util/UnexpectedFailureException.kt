package com.julianfortune.beanstock.core.util

data class UnexpectedFailureException(
    override val message: String?, override val cause: Throwable? = null
): Throwable(message, cause)