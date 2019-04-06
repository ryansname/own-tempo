package com.github.ryansname.own_tempo.applitcation_state

import com.github.ryansname.own_tempo.applitcation_state.model.RecordId
import com.github.ryansname.own_tempo.model.Ticket
import com.github.ryansname.own_tempo.model.WorkLog

sealed class ApplicationEvent {

    /**
     * The record identified by [recordId] has been restarted, or a new record if [recordId] is null.
     */
    data class RecordStarted(val recordId: RecordId?) : ApplicationEvent()

    data class RecordCompleted(val recordId: RecordId): ApplicationEvent()
    data class RecordModified(
        val recordId: RecordId,
        val label: String? = null,
        val ticket: Ticket? = null,
        val workLog: WorkLog? = null,
        val remark: String? = null
    ) : ApplicationEvent()
}
