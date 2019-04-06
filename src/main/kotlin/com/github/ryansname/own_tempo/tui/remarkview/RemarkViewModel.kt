package com.github.ryansname.own_tempo.tui.remarkview

import com.github.ryansname.own_tempo.model.Ticket
import java.time.LocalDate

data class RemarkViewModel(
    val ticket: Ticket?,
    val date: LocalDate,
    val remark: String
) {
    constructor() : this(
        null,
        LocalDate.now(),
        ""
    )
}
