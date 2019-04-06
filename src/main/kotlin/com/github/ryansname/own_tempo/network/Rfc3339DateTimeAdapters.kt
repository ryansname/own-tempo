package com.github.ryansname.own_tempo.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime

class Rfc3339LocalDateTimeAdapter : JsonAdapter<LocalDateTime>() {
    override fun fromJson(reader: JsonReader): LocalDateTime? {
        val string = reader.nextString()
        return LocalDateTime.parse(string)
    }

    override fun toJson(writer: JsonWriter, value: LocalDateTime?) {
        writer.value(value?.toString())
    }
}

class Rfc3339OffsetDateTimeAdapter : JsonAdapter<OffsetDateTime>() {
    override fun fromJson(reader: JsonReader): OffsetDateTime? {
        val string = reader.nextString()
        return OffsetDateTime.parse(string)
    }

    override fun toJson(writer: JsonWriter, value: OffsetDateTime?) {
        writer.value(value?.toString())
    }
}

class Rfc3339LocalDateAdapter : JsonAdapter<LocalDate>() {
    override fun fromJson(reader: JsonReader): LocalDate? {
        val string = reader.nextString()
        return LocalDate.parse(string)
    }

    override fun toJson(writer: JsonWriter, value: LocalDate?) {
        writer.value(value?.toString())
    }
}
