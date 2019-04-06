package com.github.ryansname.own_tempo.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Debounces the given executable over given duration.
 */
class Debouncer<V> internal constructor(
    period: Duration,
    executable: suspend CoroutineScope.(V) -> Unit,
    coroutineContext: CoroutineContext = EmptyCoroutineContext
) {
    private val channel: Channel<V> = Channel(Channel.CONFLATED)

    init {
        GlobalScope.launch(coroutineContext) {
            var job: Job = Job().also { it.complete() }

            for (value in channel) {
                job.cancel()
                job = launch {
                    delay(period.toMillis())
                    executable(value)
                }
            }
        }
    }

    /**
     * Submits a value to be potentially processed.
     *
     * The first value submitted using [submit] will run instantly on submission.
     * The final value submitted before the limit expires will be run once the rate limit is relieved.
     *
     * Apart from these two values all other submitted values will be silently ignored.
     */
    fun submit(value: V) = runBlocking {
        channel.send(value)
    }
}

/**
 * @see Debouncer
 */
fun <V> debounce(
    period: Duration,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    executable: suspend CoroutineScope.(V) -> Unit
): Debouncer<V> = Debouncer(period, executable, coroutineContext)
