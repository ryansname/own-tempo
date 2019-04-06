package com.github.ryansname.own_tempo.utils

import java.time.Duration

inline fun <V> Iterable<V>.replaceIndex(index: Int, block: (V) -> V): List<V> {
    return mapIndexed { i, v -> if (index == i) block(v) else v }
}

fun Iterable<Duration>.sum(): Duration = fold(Duration.ZERO) { acc, duration -> acc + duration }
