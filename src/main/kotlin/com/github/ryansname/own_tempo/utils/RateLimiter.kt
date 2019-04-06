package com.github.ryansname.own_tempo.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Rate limits the given executable to at most one execution per given duration.
 *
 * The first value submitted using [submit] will run instantly on submission.
 * The final value submitted before the limit expires will be run once the rate limit is relieved.
 */
class RateLimiter<V> internal constructor(
    period: Duration,
    executable: suspend CoroutineScope.(V) -> Unit,
    coroutineContext: CoroutineContext = EmptyCoroutineContext
) {
    private val channel: Channel<V> = Channel(Channel.CONFLATED)

    init {
        GlobalScope.launch(coroutineContext) {
            for (value in channel) {
                val currentJob = launch {
                    executable(value)
                }

                delay(period.toMillis())
                currentJob.join()
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
 * @see RateLimiter
 */
fun <V> rateLimit(
    period: Duration,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    executable: suspend CoroutineScope.(V) -> Unit
): RateLimiter<V> = RateLimiter(period, executable, coroutineContext)
