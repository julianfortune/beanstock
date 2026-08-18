package com.julianfortune.glacier.core.util

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun <T> Flow<T>.throttle(minimumDelay: Duration): Flow<T> = flow {
    var lastEmittedAtEpochMillis = 0L
    val delayMs = minimumDelay.inWholeMilliseconds

    collect { value ->
        val currentEpochMillis = System.currentTimeMillis()
        if (currentEpochMillis - lastEmittedAtEpochMillis >= delayMs) {
            lastEmittedAtEpochMillis = currentEpochMillis
            emit(value)
        }
    }
}

fun <T> Flow<T>.throttleLatest(minimumDelay: Duration): Flow<T> = channelFlow {
    var lastEmittedAtEpochMs = 0L
    var timerJob: kotlinx.coroutines.Job? = null

    coroutineScope {
        collect { value ->
            val currentEpochMs = System.currentTimeMillis()
            val elapsedMs = currentEpochMs - lastEmittedAtEpochMs

            timerJob?.cancel()

            when {
                elapsedMs > minimumDelay.inWholeMilliseconds -> {
                    lastEmittedAtEpochMs = currentEpochMs
                    send(value)
                }

                else -> {
                    timerJob = launch {
                        delay(minimumDelay - elapsedMs.milliseconds)

                        lastEmittedAtEpochMs = currentEpochMs
                        send(value)
                    }
                }
            }
        }
    }
}