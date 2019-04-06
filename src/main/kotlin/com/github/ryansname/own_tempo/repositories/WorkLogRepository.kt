package com.github.ryansname.own_tempo.repositories

import com.github.ryansname.own_tempo.model.WorkLog
import com.github.ryansname.own_tempo.network.atlassian.TempoClient
import com.github.ryansname.own_tempo.network.atlassian.types.TempoCreateWorkLog
import com.github.ryansname.own_tempo.network.atlassian.types.TempoUpdateWorkLog
import java.time.Duration
import java.time.LocalDate

class WorkLogRepository(
    private val tempoClient: TempoClient
) {
    suspend fun update(workLogId: String, time: Duration, remark: String, date: LocalDate): WorkLog {
        return tempoClient.updateWorkLog(
            TempoUpdateWorkLog(
                workLogId.toLong(),
                remark,
                date,
                time.seconds,
                "ryan"
            )
        )
            .let { tempoClient.fetchWorkLog(it.originId) }
            .let(::WorkLog)
    }

    suspend fun submit(ticketId: String, time: Duration, remark: String, date: LocalDate): WorkLog {
        return tempoClient.createWorkLog(
            TempoCreateWorkLog(
                ticketId.toLong(),
                remark,
                date,
                time.seconds,
                "ryan"
            )
        )
            .first()
            .let { tempoClient.fetchWorkLog(it.originId) }
            .let(::WorkLog)
    }

    suspend fun fetch(date: LocalDate): List<WorkLog> {
        return tempoClient.fetchWorkLogs(date).map(::WorkLog)
    }
}
