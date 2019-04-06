package com.github.ryansname.own_tempo.tui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.internal.MainDispatcherFactory
import kotlinx.coroutines.newSingleThreadContext
import kotlin.coroutines.CoroutineContext

private val dispatcher = newSingleThreadContext("Lanterna Main")

sealed class LanternaMainDispatcher : MainCoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatcher.dispatch(context, block)
    }
}

@UseExperimental(InternalCoroutinesApi::class)
internal class LanternaDispatcherFactory : MainDispatcherFactory {
    override val loadPriority: Int
        get() = 0

    override fun createDispatcher(allFactories: List<MainDispatcherFactory>) = Lanterna
}

internal object Lanterna : LanternaMainDispatcher() {
    @ExperimentalCoroutinesApi
    override val immediate: MainCoroutineDispatcher
        get() = this
}
