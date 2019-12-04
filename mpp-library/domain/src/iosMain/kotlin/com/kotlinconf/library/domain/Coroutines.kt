package com.kotlinconf.library.domain

import kotlinx.coroutines.*
import platform.darwin.*
import kotlin.coroutines.CoroutineContext


internal actual val Dispatchers.UI: CoroutineDispatcher get() = UIDispatcher()

@UseExperimental(InternalCoroutinesApi::class)
internal class UIDispatcher : CoroutineDispatcher(), Delay {
    private val mQueue = dispatch_get_main_queue()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(mQueue) {
            block.run()
        }
    }

    override fun scheduleResumeAfterDelay(
        timeMillis: Long,
        continuation: CancellableContinuation<Unit>
    ) {
        dispatch_after(
            `when` = dispatch_time(
                DISPATCH_TIME_NOW,
                timeMillis * NSEC_PER_MSEC.toLong()
            ),
            queue = mQueue
        ) {
            val result = continuation.tryResume(Unit)
            if (result != null) {
                continuation.completeResume(result)
            }
        }
    }

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
        var disposed = false
        dispatch_after(
            `when` = dispatch_time(
                DISPATCH_TIME_NOW,
                timeMillis * NSEC_PER_MSEC.toLong()
            ),
            queue = mQueue
        ) {
            if (disposed) return@dispatch_after

            block.run()
        }
        return object : DisposableHandle {
            override fun dispose() {
                disposed = true
            }
        }
    }
}