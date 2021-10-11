package uz.texnopos.test

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class Timer(repeatMillis: Long) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    private var count = 0
    private fun startCoroutineTimer(repeatMillis: Long = 0) =
        scope.launch(Dispatchers.IO) {
            while (keepRunning.get()) {
                running.invoke(count++)
                delay(repeatMillis)
            }
        }

    private var running: (count: Int) -> Unit = {}
    fun timerRunningTime(running: (count: Int) -> Unit) {
        this.running = running
    }

    val timer: Job = startCoroutineTimer(repeatMillis)

    fun startTimer() {
        count = 0
        timer.start()
    }

    private val keepRunning = AtomicBoolean(true)
    private fun shutdown() {
        keepRunning.set(false)
    }

    fun cancelTimer() {
        shutdown()
        timer.cancel("cancel() called")
    }
}