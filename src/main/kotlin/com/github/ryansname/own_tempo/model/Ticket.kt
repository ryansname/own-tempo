package com.github.ryansname.own_tempo.model

import com.github.ryansname.own_tempo.network.atlassian.types.JiraTicket
import com.github.ryansname.own_tempo.network.atlassian.types.JiraTicketPickerTicket

data class Ticket(
    val id: String,
    val key: String,
    val summary: String
) {
    constructor(jiraTicket: JiraTicket) : this(
        id = jiraTicket.id.toString(),
        key = jiraTicket.key,
        summary = jiraTicket.summary
    )

    constructor(ticketId: Long, jiraTicket: JiraTicketPickerTicket) : this(
        id = ticketId.toString(),
        key = jiraTicket.key,
        summary = jiraTicket.summaryText
    )

    val keyAndSummary = String.format("%8s - %s", key, summary)
}
