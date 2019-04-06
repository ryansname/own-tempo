package com.github.ryansname.own_tempo.network.atlassian

import com.github.ryansname.own_tempo.network.atlassian.types.JiraTicketPickerResult
import com.github.ryansname.own_tempo.network.atlassian.types.PaginatedJiraTickets
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketClient {
    @GET("/rest/api/2/search")
    suspend fun search(
        @Query("jql") jql: String,
        @Query("startAt") startAt: Int = 0,
        @Query("maxResults") count: Int = 50
    ): PaginatedJiraTickets

    @GET("/rest/api/2/issue/picker")
    suspend fun pickerSearch(
        @Query("query") query: String,
        @Query("currentJQL") filter: String = "project in projectsWhereUserHasPermission(\"Work on issues\")"
    ): JiraTicketPickerResult
}
