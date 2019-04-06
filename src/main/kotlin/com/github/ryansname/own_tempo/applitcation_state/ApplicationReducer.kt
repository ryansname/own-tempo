package com.github.ryansname.own_tempo.applitcation_state

import com.github.ryansname.own_tempo.applitcation_state.model.ActiveRecord
import com.github.ryansname.own_tempo.applitcation_state.model.ApplicationModel
import com.github.ryansname.own_tempo.applitcation_state.model.TimeBlock
import com.github.ryansname.own_tempo.applitcation_state.model.modifyRecord
import com.github.ryansname.own_tempo.applitcation_state.model.nextRecordId
import com.github.ryansname.own_tempo.tui.Reducer
import java.time.LocalDateTime

class ApplicationReducer : Reducer<ApplicationModel, ApplicationEvent> {
    override fun reduce(model: ApplicationModel, event: ApplicationEvent): ApplicationModel {
        return when (event) {
            is ApplicationEvent.RecordStarted -> {
                val newRecords = model.records.toMutableMap()
                val newRecordStack = model.recordStack.toMutableList()

                // Close the last record if there we one
                model.recordStack.lastOrNull()?.let {
                    newRecords.computeIfPresent(it) { _, record -> record.closeLastTimeBlock() }
                }

                // Start a new record, add it to the stack
                if (event.recordId == null || event.recordId !in newRecords) {
                    val newRecord = ActiveRecord(
                        nextRecordId(),
                        newRecords.size.toString(),
                        "",
                        null,
                        null,
                        false,
                        listOf(TimeBlock(LocalDateTime.now(), null))
                    )
                    newRecords[newRecord.id] = newRecord
                    newRecordStack.add(newRecord.id)
                } else {
                    val newRecord = newRecords[event.recordId]!!
                    newRecords.computeIfPresent(event.recordId) { _, record -> record.startNewTimeBlock() }
                    newRecordStack.remove(newRecord.id)
                    newRecordStack.add(newRecord.id)
                }

                model.copy(
                    recordStack = newRecordStack,
                    records = newRecords
                )
            }

            is ApplicationEvent.RecordCompleted -> {
                val newRecords = model.records.toMutableMap()
                newRecords.computeIfPresent(event.recordId) { _, record -> record.completeRecord() }

                val newStack = model.recordStack.dropLast(1)
                val newActiveRecord = newStack.lastOrNull()?.let { newRecords[it] }

                if (newActiveRecord != null && !newActiveRecord.isRecording) {
                    newRecords.computeIfPresent(newActiveRecord.id) { _, record -> record.startNewTimeBlock() }
                }

                return model.copy(
                    recordStack = newStack,
                    records = newRecords
                )
            }
            is ApplicationEvent.RecordModified -> model.modifyRecord(event.recordId) {
                var result = it
                if (event.label != null) result = result.copy(label = event.label)
                if (event.ticket != null) result = result.copy(ticket = event.ticket)
                if (event.workLog != null) result = result.copy(workLog = event.workLog)
                if (event.remark != null) result = result.copy(remark = event.remark)

                result
            }
        }
    }
}
