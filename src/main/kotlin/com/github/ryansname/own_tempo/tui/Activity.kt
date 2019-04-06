package com.github.ryansname.own_tempo.tui

import com.github.ryansname.own_tempo.model.Ticket
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.screen.Screen
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

abstract class Activity<M, E, R>(
    private val tuiScreen: TuiScreen?,
    private val reducer: Reducer<M, E>,
    private var _model: M
) : CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Main) {
    internal val result: CompletableDeferred<R> = CompletableDeferred(parent = coroutineContext[Job.Key])

    protected val model get() = _model

    fun postInitialize(init: ((M) -> M)?) {
        if (init != null) {
            _model = init.invoke(_model)
        }
    }

    open fun onBeforeDisplay() {}

    open fun onPause() {}
    open fun onResume() {}

    fun stopped() {
        // blank?
    }

    protected fun post(event: E) {
        _model = reducer.reduce(_model, event)
        repaint()
    }

    abstract fun paint(screen: Screen)

    protected fun repaint() {
        tuiScreen?.repaint()
    }

    abstract fun handleInput(input: KeyStroke)  // TODO: Wrap lib keystroke?
}

interface Reducer<M, E> {
    fun reduce(model: M, event: E): M
}

class UnitReducer<M: Any> : Reducer<M, Unit> {
    override fun reduce(model: M, event: Unit) = model
}
