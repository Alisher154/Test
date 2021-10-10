package uz.texnopos.test

import kotlinx.coroutines.*

class Timer(repeatMillis: Long, task: (count: Int, timer: Job) -> Unit) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    private var count = 0
    private fun startCoroutineTimer(repeatMillis: Long = 0, action: (count: Int, timer: Job) -> Unit, ) =
        scope.launch(Dispatchers.IO) {
            while (true) {
                action(count++, timer)
                delay(repeatMillis)
            }
        }

    private val timer: Job = startCoroutineTimer(repeatMillis, task)

    fun startTimer() {
        count = 0
        timer.start()
    }

    fun cancelTimer() {
        timer.cancel()
    }
}