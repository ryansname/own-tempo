package com.github.ryansname.own_tempo.network.atlassian.types

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaginatedJiraTickets(
    val total: Int,
    val issues: List<JiraTicket>
)

@JsonClass(generateAdapter = true)
data class JiraTicket(
    val id: Long,
    val key: String,
    val fields: Map<String, Any>
) {
    val summary: String by fields
}

@JsonClass(generateAdapter = true)
data class JiraTicketPickerResult(
    val sections: List<JiraTicketPickerSection>
)

@JsonClass(generateAdapter = true)
data class JiraTicketPickerSection(
    val label: String,
    val issues: List<JiraTicketPickerTicket>
)

@JsonClass(generateAdapter = true)
data class JiraTicketPickerTicket(
    val key: String,
    val summaryText: String
)
