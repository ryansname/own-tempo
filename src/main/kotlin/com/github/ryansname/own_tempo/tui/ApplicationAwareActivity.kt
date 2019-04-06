package com.github.ryansname.own_tempo.tui

import com.github.ryansname.own_tempo.applitcation_state.ApplicationEventBus
import com.github.ryansname.own_tempo.applitcation_state.model.ApplicationModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class ApplicationAwareActivity<M, E, R>(
    tuiScreen: TuiScreen?,
    private val applicationEventBus: ApplicationEventBus,
    reducer: Reducer<M, E>,
    _model: M
) : Activity<M, E, R>(tuiScreen, reducer, _model) {
    protected var applicationModel: ApplicationModel = ApplicationModel()
        private set

    override fun onBeforeDisplay() {
        super.onBeforeDisplay()

        launch {
            applicationEventBus.watchState().collect {
                applicationModel = it
                repaint()
            }
        }
    }
}
