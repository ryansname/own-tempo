package com.github.ryansname.own_tempo.tui.remarkview

import com.github.ryansname.own_tempo.model.Ticket
import com.github.ryansname.own_tempo.repositories.WorkLogRepository
import com.github.ryansname.own_tempo.tui.Activity
import com.github.ryansname.own_tempo.tui.ActivityManager
import com.github.ryansname.own_tempo.tui.TuiScreen
import com.github.ryansname.own_tempo.tui.remarkview.RemarkViewEvent.RemarkChanged
import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import java.time.LocalDate

class RemarkView(
    screen: TuiScreen,
    private val activityManager: ActivityManager,
    reducer: RemarkViewReducer,
    private val workLogRepository: WorkLogRepository,
    initialModel: RemarkViewModel = RemarkViewModel()
) : Activity<RemarkViewModel, RemarkViewEvent, String>(screen, reducer, initialModel) {

    override fun handleInput(input: KeyStroke) {
        when (input.keyType) {
            KeyType.Escape -> activityManager.stopActivity(this)

            KeyType.Backspace -> post(RemarkChanged(model.remark.dropLast(1)))
            KeyType.Character -> post(RemarkChanged(model.remark + input.character))
            KeyType.Enter -> if (model.remark.lastOrNull() == '\\') {
                // Allow escaping to add a newline
                post(RemarkChanged(model.remark.dropLast(1) + "\n"))
            } else {
                result.complete(model.remark.trim())
                activityManager.stopActivity(this)
            }
        }
    }

    override fun paint(screen: Screen) {
        screen.newTextGraphics().run {
            val dateString = model.date.toString()
            model.ticket?.let { putString(1, 0, it.keyAndSummary) }
            putString( size.columns - 2 - dateString.length, 0, dateString)

            var row = 2
            var col = 1
            model.remark.chunked(1).forEach {
                if (it == "\n") {
                    col = 1
                    row += 1
                } else {
                    putString(col, row, it)
                    col += 1
                }
            }
            putString(col, row, " ", listOf(SGR.REVERSE))
        }
    }
}

suspend fun ActivityManager.startRemarkView(ticket: Ticket?, date: LocalDate, currentRemark: String? = null): String? {
    return startActivity(RemarkView::class) { it.copy(
        ticket = ticket,
        date = date,
        remark = currentRemark?.takeIf { it.isNotBlank() }?.plus("\n") ?: ""
    )}
}
