package com.github.ryansname.own_tempo.network.atlassian.types

import java.time.LocalDate

data class TempoCreateWorkLog(
    val originTaskId: Long,
    val comment: String,
    val started: LocalDate,
    val timeSpentSeconds: Long,
    val worker: String
)

data class TempoUpdateWorkLog(
    val originId: Long,
    val comment: String,
    val started: LocalDate,
    val timeSpentSeconds: Long,
    val worker: String
)
