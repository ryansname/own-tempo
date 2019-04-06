package com.github.ryansname.own_tempo.tui.remarkview

sealed class RemarkViewEvent {
    data class RemarkChanged(val newRemark: String) : RemarkViewEvent()
}
