package com.github.ryansname.own_tempo.applitcation_state.model

import com.github.ryansname.own_tempo.model.Ticket
import com.github.ryansname.own_tempo.model.WorkLog
import com.github.ryansname.own_tempo.utils.replaceIndex
import com.github.ryansname.own_tempo.utils.sum
import com.github.ryansname.own_tempo.utils.truncateToMinutes
import com.github.ryansname.own_tempo.utils.truncateToSeconds
import java.time.Duration
import java.time.LocalDateTime

data class ActiveRecord(
    val id: RecordId,
    val label: String,
    val remark: String,
    val ticket: Ticket?,
    val workLog: WorkLog?,
    val completed: Boolean,
    val timeBlocks: List<TimeBlock>
) {
    val isRecording= timeBlocks.lastOrNull()?.endTime == null
    val duration get() = timeBlocks.map { it.duration }.sum().truncateToSeconds()
    val recorded = workLog != null
    val dirty get() = workLog == null
                || duration > Duration.ZERO && duration.truncateToMinutes() != workLog.timeSpent.truncateToMinutes()
                || workLog.comment != remark

    fun startNewTimeBlock() = copy(timeBlocks = timeBlocks + TimeBlock(LocalDateTime.now(), null))

    fun closeLastTimeBlock(): ActiveRecord {
        return copy(timeBlocks = timeBlocks.replaceIndex(timeBlocks.lastIndex) {
            it.copy(endTime = LocalDateTime.now())
        })
    }

    fun completeRecord(): ActiveRecord {
        return closeLastTimeBlock().copy(completed = true)
    }
}

data class TimeBlock(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?
) {
    val duration get() = Duration.between(startTime, endTime ?: LocalDateTime.now())!!
}
