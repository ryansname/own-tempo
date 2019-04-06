package com.github.ryansname.own_tempo.tui.recordview

import com.github.ryansname.own_tempo.applitcation_state.ApplicationEvent
import com.github.ryansname.own_tempo.applitcation_state.ApplicationEvent.*
import com.github.ryansname.own_tempo.applitcation_state.ApplicationEventBus
import com.github.ryansname.own_tempo.applitcation_state.model.ActiveRecord
import com.github.ryansname.own_tempo.repositories.WorkLogRepository
import com.github.ryansname.own_tempo.tui.ActivityManager
import com.github.ryansname.own_tempo.tui.ApplicationAwareActivity
import com.github.ryansname.own_tempo.tui.TuiScreen
import com.github.ryansname.own_tempo.tui.remarkview.startRemarkView
import com.github.ryansname.own_tempo.tui.searchview.startSearchView
import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import kotlin.properties.Delegates

class RecordView(
    screen: TuiScreen,
    private val applicationEventBus: ApplicationEventBus,
    private val activityManager: ActivityManager,
    reducer: RecordViewReducer,
    private val workLogRepository: WorkLogRepository,
    initialModel: RecordViewModel = RecordViewModel()
) : ApplicationAwareActivity<RecordViewModel, RecordViewEvent, Unit>(screen, applicationEventBus, reducer, initialModel) {

    private var debug = false
    private var update: Job? = null
    private var selectedIndex by Delegates.observable(0) { _, _, _ ->
        repaint()
    }

    private val selectedRecord: ActiveRecord?
        get() = applicationModel.records.values.toList().getOrNull(selectedIndex)

    override fun onResume() {
        super.onResume()

        if (applicationModel.records.isEmpty()) {
            applicationEventBus.post(RecordStarted(null))
        }

        update?.cancel()
        update = launch {
            while (true) {
                delay(10000)
                repaint()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        update?.cancel()
    }

    override fun handleInput(input: KeyStroke) {
        when (input.keyType) {
            KeyType.Enter -> applicationModel.recordStack.lastOrNull()?.let { applicationEventBus.post(RecordCompleted(it)) }

            KeyType.ArrowUp -> selectedIndex = selectedIndex.dec().coerceAtLeast(0)
            KeyType.ArrowDown -> selectedIndex = selectedIndex.inc().coerceAtMost(applicationModel.records.size - 1)

            KeyType.Character -> when (input.character) {
                'i' -> applicationEventBus.post(RecordStarted(null))
                'c' -> applicationEventBus.post(RecordStarted(selectedRecord?.id))
                'd' -> { debug = !debug; repaint() }
                'r' -> selectedRecord?.let {
                    launch {
                        val newRemark = activityManager
                            .startRemarkView(it.ticket, LocalDate.now(), currentRemark = it.remark) ?: return@launch

                        applicationEventBus.post(RecordModified(it.id, remark = newRemark))
                    }
                }
                'R' -> selectedRecord?.takeIf { it.remark.isBlank() }?.let {
                    applicationEventBus.post(RecordModified(it.id, remark = "Review"))
                }
                'u' -> selectedRecord?.let {
                    async { uploadTicket(it) }
                }
                'U' -> async {
                    applicationModel.records.values.forEach {
                        uploadTicket(it)
                    }
                }
                '/' -> selectedRecord?.let { record ->
                    launch {
                        val result = activityManager.startSearchView(LocalDate.now()) ?: return@launch

                        applicationEventBus.post(RecordModified(record.id, label = result.key, ticket = result))

                        activityManager.stopActivity(this@RecordView, stopInstance = false)
                    }
                }
            }
        }
    }

    override fun paint(screen: Screen) {
        screen.newTextGraphics().run {
            var row = 1
            applicationModel.records.values.forEachIndexed { i, activeRecord ->
                val sgr = if (i == selectedIndex) {
                    mutableListOf(SGR.REVERSE)  // https://groups.google.com/forum/#!topic/lanterna-discuss/aD6F8w0F8Po
                } else {
                    mutableListOf()
                }

                foregroundColor = when {
                    activeRecord.recorded && activeRecord.dirty -> TextColor.ANSI.RED
                    activeRecord.completed && activeRecord.recorded -> TextColor.ANSI.GREEN
                    activeRecord.completed -> TextColor.ANSI.YELLOW
                    activeRecord.recorded -> TextColor.ANSI.CYAN
                    else -> TextColor.ANSI.DEFAULT
                }

                val indicator = if (activeRecord.isRecording) "> " else ""
                putString(1, row++, "$indicator${activeRecord.label} - ${activeRecord.duration}", sgr)
                // TODO: Add a flag to turn this off?
                if (activeRecord.remark.isNotBlank()) {
                    putString(5, row++, activeRecord.remark)
                }

                if (debug) {
                    activeRecord.timeBlocks.forEach { block ->
                        val format = "${block.startTime} - ${block.endTime ?: ""}"

                        putString(5, row++, format)
                    }
                }
            }
        }
    }

    private suspend fun uploadTicket(record: ActiveRecord) {
        record.ticket ?: return
        // TODO: Error handling
        val workLog = if (record.workLog != null) {
            workLogRepository.update(
                record.workLog.id,
                record.duration.coerceAtLeast(Duration.ofSeconds(1)),
                record.remark.takeIf { r -> r.isNotBlank() } ?: "TODO",
                LocalDate.now()
            )
        } else {
            workLogRepository.submit(
                record.ticket.id,
                record.duration.coerceAtLeast(Duration.ofSeconds(1)),
                record.remark.takeIf { r -> r.isNotBlank() } ?: "TODO",
                LocalDate.now()
            )
        }

        applicationEventBus.post(RecordModified(record.id, workLog = workLog))
    }
}

suspend fun ActivityManager.startRecordView(): Unit? {
    return startActivity(RecordView::class)
}
