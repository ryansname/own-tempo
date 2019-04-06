package com.github.ryansname.own_tempo.tui.searchview

import com.github.ryansname.own_tempo.model.Ticket
import java.time.LocalDate

data class SearchViewModel(
    val date: LocalDate,  // TODO, have an overall model?
    val matchingTickets: List<Ticket>
) {
    constructor() : this(
        LocalDate.now(),
        listOf()
    )
}
