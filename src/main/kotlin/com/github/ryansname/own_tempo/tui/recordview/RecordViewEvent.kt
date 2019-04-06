package com.github.ryansname.own_tempo.tui.recordview

import com.github.ryansname.own_tempo.applitcation_state.model.ApplicationModel

sealed class RecordViewEvent {
    class ApplicationStateChanged(val applicationModel: ApplicationModel) : RecordViewEvent()
}
