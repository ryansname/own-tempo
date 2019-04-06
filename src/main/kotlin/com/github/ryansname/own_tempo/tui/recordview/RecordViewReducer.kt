package com.github.ryansname.own_tempo.tui.recordview

import com.github.ryansname.own_tempo.tui.Reducer

class RecordViewReducer : Reducer<RecordViewModel, RecordViewEvent> {
    override fun reduce(model: RecordViewModel, event: RecordViewEvent): RecordViewModel {
        return when (event) {
            is RecordViewEvent.ApplicationStateChanged -> model.copy(applicationModel = event.applicationModel)
        }
    }
}
