package com.github.ryansname.own_tempo.applitcation_state

import com.github.ryansname.own_tempo.applitcation_state.model.ApplicationModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Bus for collecting events relevant to the state of the application as a whole.
 */
class ApplicationEventBus(
    private val applicationReducer: ApplicationReducer,
    private var currentModel: ApplicationModel
) {
    private val channel = ConflatedBroadcastChannel(currentModel)
    val model get() = currentModel

    fun post(event: ApplicationEvent) {
        currentModel = applicationReducer.reduce(currentModel, event)
        channel.offer(currentModel)
    }

    fun watchState(): Flow<ApplicationModel> = channel.asFlow().distinctUntilChanged()
}
