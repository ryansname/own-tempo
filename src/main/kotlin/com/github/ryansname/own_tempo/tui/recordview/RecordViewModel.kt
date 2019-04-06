package com.github.ryansname.own_tempo.tui.recordview

import com.github.ryansname.own_tempo.applitcation_state.model.ApplicationModel

data class RecordViewModel(
    val applicationModel: ApplicationModel
) {
    constructor() : this(
        ApplicationModel()
    )
}
