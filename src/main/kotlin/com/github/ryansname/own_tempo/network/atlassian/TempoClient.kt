package com.github.ryansname.own_tempo.network.atlassian

import com.github.ryansname.own_tempo.network.atlassian.types.OriginId
import com.github.ryansname.own_tempo.network.atlassian.types.TempoCreateWorkLog
import com.github.ryansname.own_tempo.network.atlassian.types.TempoUpdateWorkLog
import com.github.ryansname.own_tempo.network.atlassian.types.TempoWorkLog
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface TempoClient {
    @GET("/rest/tempo-timesheets/3/worklogs/")
    suspend fun fetchWorkLogs(
        @Query("dateFrom") date: LocalDate,
        @Query("dateTo") dateTo: LocalDate = date
    ): List<TempoWorkLog>

    @GET("/rest/tempo-timesheets/3/worklogs/{workLogId}")
    suspend fun fetchWorkLog(
        @Path("workLogId") workLogId: Int
    ): TempoWorkLog

    @POST("/rest/tempo-timesheets/4/worklogs/")
    suspend fun createWorkLog(@Body workLog: TempoCreateWorkLog): List<OriginId>

    @PUT("/rest/tempo-timesheets/4/worklogs/{workLogId}")
    suspend fun updateWorkLog(
        @Body workLog: TempoUpdateWorkLog,
        @Path("workLogId") workLogId: Long = workLog.originId
    ): OriginId
}
