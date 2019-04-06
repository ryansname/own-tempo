package com.github.ryansname.own_tempo.network.atlassian.types

import com.squareup.moshi.JsonClass
import java.time.Duration
import java.time.LocalDateTime

@JsonClass(generateAdapter = true) data class OriginId(val originId: Int)

@JsonClass(generateAdapter = true)
data class TempoWorkLog(
    val self: String,

    val id: Int,
    val comment: String,
    val issue: TempoWorklogIssue,

    val timeSpentSeconds: Int,
    val dateStarted: LocalDateTime,  // Note: This only works because Jira is set to the same TZ as me...
    val author: TempoWorklogAuthor

) {
    val timeSpent by lazy { Duration.ofSeconds(timeSpentSeconds.toLong())!! }
}

@JsonClass(generateAdapter = true)
data class TempoWorklogIssue(
    val self: String,

    val id: Int,
    val key: String,
    val summary: String
)

@JsonClass(generateAdapter = true)
data class TempoWorklogAuthor(
    val self: String,

    val name: String,
    val displayName: String
)
