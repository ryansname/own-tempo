package com.github.ryansname.own_tempo.tui

import com.github.ryansname.own_tempo.model.Ticket
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface ActivityManager {
    fun <A: Activity<*, *, *>> createActivity(activityClass: KClass<A>): A
    suspend fun <M, A: Activity<M, *, R>, R> startActivity(activity: A, init: ((M) -> M)? = null): R?
    fun stopActivity(instance: Activity<*, *, *>, stopInstance: Boolean = true)

    suspend fun <M, A: Activity<M, *, R>, R> startActivity(activityClass: KClass<A>, init: ((M) -> M)? = null): R? {
        return startActivity(createActivity(activityClass), init)
    }
}
