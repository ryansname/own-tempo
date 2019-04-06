package com.github.ryansname.own_tempo.model

import com.github.ryansname.own_tempo.network.atlassian.types.TempoWorkLog
import java.time.Duration
import java.time.LocalDate

data class WorkLog(
    val id: String,

    val issueKey: String,
    val issueSummary: String,

    val comment: String,

    val timeSpent: Duration,
    val date: LocalDate
) {
    constructor(tempoWorkLog: TempoWorkLog) : this(
        id = tempoWorkLog.id.toString(),
        issueKey = tempoWorkLog.issue.key,
        issueSummary = tempoWorkLog.issue.summary,
        comment = tempoWorkLog.comment,
        timeSpent = tempoWorkLog.timeSpent,
        date = tempoWorkLog.dateStarted.toLocalDate()
    )
}
