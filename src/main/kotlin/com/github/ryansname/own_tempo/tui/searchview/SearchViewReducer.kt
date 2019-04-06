package com.github.ryansname.own_tempo.tui.searchview

import com.github.ryansname.own_tempo.tui.Reducer

class SearchViewReducer : Reducer<SearchViewModel, SearchViewEvent> {
    override fun reduce(model: SearchViewModel, event: SearchViewEvent): SearchViewModel {
        return when (event) {
            is SearchViewEvent.TicketsMatched -> {
                model.copy(matchingTickets = event.tickets)
            }
        }
    }
}
