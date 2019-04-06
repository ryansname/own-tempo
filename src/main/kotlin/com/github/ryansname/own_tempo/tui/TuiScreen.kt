package com.github.ryansname.own_tempo.tui

import com.github.ryansname.own_tempo.applitcation_state.ApplicationEventBus
import com.github.ryansname.own_tempo.model.Ticket
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.koin.core.KoinComponent
import java.io.Closeable
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

class TuiScreen(
    private val applicationEventBus: ApplicationEventBus
) : ActivityManager, Closeable, CoroutineScope, KoinComponent {
    private val screenJob = Job()
    override val coroutineContext: CoroutineContext
        get() = screenJob + Dispatchers.Main
    private val interruptChannel = Channel<Unit>(Channel.RENDEZVOUS)

    @Volatile
    private var running: Boolean = true

    private val activityStack: MutableList<Activity<*, *, *>> = mutableListOf()
    private val currentActivity
        get() = activityStack.lastOrNull()

    private val screen: Screen
    init {
        val terminalFactory = DefaultTerminalFactory()
        screen = terminalFactory.createScreen()!!

        screen.startScreen()
        screen.cursorPosition = null
    }

    fun start() = launch {
        mainLoop@while (running) {
            val inputAsync = async(Dispatchers.IO) { screen.readInput() }

            val input = select<KeyStroke?> {
                inputAsync.onAwait { it }
                interruptChannel.onReceive { null }
            }
            if (input == null) {
                println("Exiting")
                break@mainLoop
            }

            if (input.keyType == KeyType.EOF || (input.character == 'q' && input.isCtrlDown)) {
                break@mainLoop
            }
            screen.doResizeIfNecessary()

            currentActivity?.handleInput(input)
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        screen.close()
    }

    override fun <A: Activity<*, *, *>> createActivity(activityClass: KClass<A>): A {
        return getKoin().get(activityClass, null, parameters = null)
    }

    override suspend fun <M, A: Activity<M, *, R>, R> startActivity(activity: A, init: ((M) -> M)?): R? {
        currentActivity?.onPause()

        activityStack += activity

        activity.postInitialize(init)
        activity.onBeforeDisplay()

        repaint()

        activity.onResume()

        return activity.result.await()
    }

    override fun stopActivity(instance: Activity<*, *, *>, stopInstance: Boolean) {
        require(instance in activityStack) { "Instance is not a currently running activity? $instance" }

        if (instance !in activityStack) {
            return
        }

        if (!stopInstance && currentActivity == instance) {
            return
        }

        do {
            val lastActivity = activityStack.removeAt(activityStack.lastIndex)

            lastActivity.onPause()

            lastActivity.stopped()
            lastActivity.cancel()

            currentActivity?.onResume()

        } while ((stopInstance && lastActivity != instance) || (!stopInstance && currentActivity != instance))

        repaint()
    }

    fun repaint() {
        screen.clear()

        currentActivity?.paint(screen)

        screen.refresh()
    }

    override fun close() = runBlocking {
        running = false

        coroutineContext.cancel()
        if (!coroutineContext.isActive) {
            screen.close()
        } else {
            interruptChannel.send(Unit)
        }

        screenJob.join()
    }
}
