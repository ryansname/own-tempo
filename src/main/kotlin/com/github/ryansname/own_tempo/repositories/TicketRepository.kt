package com.github.ryansname.own_tempo.repositories

import com.github.ryansname.own_tempo.model.Ticket
import com.github.ryansname.own_tempo.network.atlassian.TicketClient

class TicketRepository(
    private val ticketClient: TicketClient
) {
    suspend fun search(searchString: String): List<Ticket> {
        val pickerResult = ticketClient.pickerSearch(searchString)

        return pickerResult.sections
            .flatMap { it.issues }
            .distinctBy { it.key }
            .mapIndexed { index, match ->
                if (index == 0) {  // TODO: Not this
                    // TODO: I could batch these up and cache em
                    ticketClient.search("key = ${match.key}").issues[0].id to match
                } else {
                    -1L to match
                }
            }
            .map { Ticket(it.first, it.second) }
            .distinct()
    }
}
