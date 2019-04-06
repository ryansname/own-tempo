package com.github.ryansname.own_tempo.network.atlassian

import com.github.ryansname.own_tempo.network.Rfc3339LocalDateAdapter
import com.github.ryansname.own_tempo.network.Rfc3339LocalDateTimeAdapter
import com.github.ryansname.own_tempo.network.Rfc3339OffsetDateTimeAdapter
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.Closeable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime

class WebClient: Closeable {
    private val moshi = Moshi.Builder()
        .add(LocalDate::class.java, Rfc3339LocalDateAdapter())
        .add(LocalDateTime::class.java, Rfc3339LocalDateTimeAdapter())
        .add(OffsetDateTime::class.java, Rfc3339OffsetDateTimeAdapter())
        .build()

    private val authInterceptor = Interceptor { chain->
        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization", System.getenv("AUTH_HEADER"))
            .build()

        chain.proceed(newRequest)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addNetworkInterceptor {
            val response = it.proceed(it.request())
            if (!response.isSuccessful) {
                println(response.peekBody(1024).string())
            }

            response
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://jira.motiondesign.co.nz")  // TODO: Configurable!
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val tempoClient by lazy { getClient<TempoClient>() }
    val ticketClient: TicketClient by lazy { getClient<TicketClient>() }

    private inline fun <reified C> getClient(): C = retrofit.create(C::class.java)

    override fun close() {
        // Jank town to exit immediately on completion
        (retrofit.callFactory() as? OkHttpClient)?.dispatcher()?.executorService()?.shutdown()
    }
}
