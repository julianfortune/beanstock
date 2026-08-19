package com.julianfortune.beanstock.ui.common.data

sealed interface Dynamic<out VALUE> {
    data object Loading : Dynamic<Nothing>
    data class Present<VALUE>(val value: VALUE) : Dynamic<VALUE>
}