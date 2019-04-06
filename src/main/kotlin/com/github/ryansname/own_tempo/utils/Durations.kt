package com.github.ryansname.own_tempo.utils

import java.time.Duration

fun Duration.truncateToSeconds(): Duration = Duration.ofSeconds(seconds)
fun Duration.truncateToMinutes(): Duration = Duration.ofMinutes(toMinutes())
