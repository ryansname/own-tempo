package com.github.ryansname.own_tempo.applitcation_state.model

data class ApplicationModel(
    val recordStack: List<RecordId>,
    val records: Map<RecordId, ActiveRecord>
) {
    constructor() : this(listOf(), mapOf())
}

inline fun ApplicationModel.modifyRecord(recordId: RecordId, modifier: (ActiveRecord) -> ActiveRecord): ApplicationModel {
    val toModify = records[recordId] ?: return this

    return this.copy(records = records.toMutableMap().also { it[recordId] = modifier(toModify) })
}
