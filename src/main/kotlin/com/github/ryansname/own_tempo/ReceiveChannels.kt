package com.github.ryansname.own_tempo

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach

suspend inline fun <E> ReceiveChannel<E>.consumeConflated(
    action: (E) -> Unit
) {
    poll()?.let(action)
    consumeEach(action)
}
