package com.github.ryansname.own_tempo.tui.searchview

import com.github.ryansname.own_tempo.model.Ticket

sealed class SearchViewEvent {
    data class TicketsMatched(val tickets: List<Ticket>): SearchViewEvent()
}
