package com.github.ryansname.own_tempo.tui.searchview

import com.github.ryansname.own_tempo.model.Ticket
import com.github.ryansname.own_tempo.repositories.TicketRepository
import com.github.ryansname.own_tempo.tui.Activity
import com.github.ryansname.own_tempo.tui.ActivityManager
import com.github.ryansname.own_tempo.tui.TuiScreen
import com.github.ryansname.own_tempo.utils.debounce
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.LocalDate

class SearchView(
    screen: TuiScreen,
    private val activityManager: ActivityManager,
    private val ticketRepository: TicketRepository,
    reducer: SearchViewReducer,
    initialModel: SearchViewModel = SearchViewModel()
) : Activity<SearchViewModel, SearchViewEvent, Ticket>(screen, reducer, initialModel) {
    private var currentSearch = ""

    private val searchRateLimiter = debounce<String>(Duration.ofMillis(250), Dispatchers.IO) {
        val matches = ticketRepository.search(currentSearch)
        post(SearchViewEvent.TicketsMatched(matches))
    }

    private fun searchChanged(newSearch: String) {
        currentSearch = newSearch
        repaint()

        searchRateLimiter.submit(currentSearch)
    }

    override fun handleInput(input: KeyStroke) {
        when (input.keyType) {
            KeyType.Escape -> activityManager.stopActivity(this)
            KeyType.Enter -> model.matchingTickets.firstOrNull()?.let {
                result.complete(it)
                activityManager.stopActivity(this)
            }

            KeyType.Backspace -> searchChanged(currentSearch.dropLast(1))
            KeyType.Character -> searchChanged(currentSearch + input.character)
        }
    }

    override fun paint(screen: Screen) {
        val rows = screen.terminalSize.rows

        screen.newTextGraphics().run {
            model.matchingTickets
                .take(rows - 2)
                .forEachIndexed { index, ticket ->
                    putString(1, index, ticket.keyAndSummary)
                }

            putString(1, rows - 1, "Search: $currentSearch")
        }
    }
}

suspend fun ActivityManager.startSearchView(date: LocalDate): Ticket? {
    return startActivity(SearchView::class) { it.copy(date = date) }
}
