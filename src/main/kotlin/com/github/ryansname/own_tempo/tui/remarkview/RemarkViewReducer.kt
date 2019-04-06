package com.github.ryansname.own_tempo.tui.remarkview

import com.github.ryansname.own_tempo.tui.Reducer

class RemarkViewReducer : Reducer<RemarkViewModel, RemarkViewEvent> {
    override fun reduce(model: RemarkViewModel, event: RemarkViewEvent): RemarkViewModel {
        return when (event) {
            is RemarkViewEvent.RemarkChanged -> model.copy(remark = event.newRemark)
        }
    }
}
