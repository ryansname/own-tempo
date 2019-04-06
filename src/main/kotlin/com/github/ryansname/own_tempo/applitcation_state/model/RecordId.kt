package com.github.ryansname.own_tempo.applitcation_state.model

import kotlinx.atomicfu.atomic

data class RecordId(private val id: Int)

private val counter = atomic(1)

fun nextRecordId(): RecordId = RecordId(counter.getAndIncrement())
