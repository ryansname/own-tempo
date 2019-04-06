package com.github.ryansname.own_tempo

import com.github.ryansname.own_tempo.applitcation_state.ApplicationEventBus
import com.github.ryansname.own_tempo.applitcation_state.ApplicationReducer
import com.github.ryansname.own_tempo.applitcation_state.model.ApplicationModel
import com.github.ryansname.own_tempo.network.atlassian.WebClient
import com.github.ryansname.own_tempo.repositories.TicketRepository
import com.github.ryansname.own_tempo.repositories.WorkLogRepository
import com.github.ryansname.own_tempo.tui.ActivityManager
import com.github.ryansname.own_tempo.tui.TuiScreen
import com.github.ryansname.own_tempo.tui.recordview.RecordView
import com.github.ryansname.own_tempo.tui.recordview.RecordViewReducer
import com.github.ryansname.own_tempo.tui.recordview.startRecordView
import com.github.ryansname.own_tempo.tui.remarkview.RemarkView
import com.github.ryansname.own_tempo.tui.remarkview.RemarkViewReducer
import com.github.ryansname.own_tempo.tui.searchview.SearchView
import com.github.ryansname.own_tempo.tui.searchview.SearchViewReducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.io.Closeable

fun main(): Unit = runBlocking {

    val closeable = mutableListOf<Closeable>()
    try {
        val app = startKoin {
            printLogger()

            modules(listOf(
                module {
                    single { TuiScreen(get()).also { closeable += it } }
                    single { get<TuiScreen>() as ActivityManager }
                },

                module {
                    single { WebClient().also { closeable += it } }
                },

                module {
                    single { WorkLogRepository(get<WebClient>().tempoClient) }
                    single { TicketRepository(get<WebClient>().ticketClient) }
                },

                // Application
                module {
                    single { ApplicationReducer() }
                    single { ApplicationModel() }
                    single { ApplicationEventBus(get(), get())  }
                },


                // Search View
                module {
                    single { SearchViewReducer() }
                    factory { SearchView(get(), get(), get(), get()) }
                },

                // Remark View
                module {
                    single { RemarkViewReducer() }
                    factory { RemarkView(get(), get(), get(), get()) }
                },

                // Record View
                module {
                    single { RecordViewReducer() }
                    factory { RecordView(get(), get(), get(), get(), get()) }
                }
            ))
        }

        launch(Dispatchers.IO) {
            val osName = System.getProperty("os.name")
            if (osName.startsWith("Windows", ignoreCase = true)) {
                // TODO
            } else if (osName.contains("Linux", ignoreCase = true)) {
                val command = arrayOf("bash", "-c", """xprop -id $(xprop -root 32x '\t$0' _NET_ACTIVE_WINDOW | cut -f 2) WM_NAME""")

                val output = Runtime.getRuntime()
                    .exec(command)
                    .inputStream
                    .bufferedReader()
                    .readText()

                println(output)  // TODO: Check this
            }
        }

        val applicationJob = app.koin.get<TuiScreen>().start()

        app.koin.get<ActivityManager>().startRecordView()

        applicationJob.join()
    } catch (e: Throwable) {
        e.printStackTrace()
    } finally {
        println("Shutting down")
        closeable.reversed().forEach { it.close() }
    }

    Unit
}
