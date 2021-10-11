package uz.texnopos.test.timer

import android.util.Log
import kotlinx.coroutines.*





class CoroutineTimer(
    private val listener: CoroutineTimerListener,
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
) {

    companion object {
        const val TAG = "CoroutineTimer"
    }

    private val scope: CoroutineScope by lazy { CoroutineScope(Job() + dispatcher) }
    var state: CurrentTimerState = CurrentTimerState.STOPPED
        private set(value) {
            if (field == CurrentTimerState.DESTROYED) {
                return
            }
            field = value
        }

    fun startTimer(countDownTime: Long, delayMillis: Long = 1000) {
        when (state) {
            CurrentTimerState.RUNNING -> {
                listener.onTick(null, TimerException(TimerErrorTypes.ALREADY_RUNNING))
            }
            CurrentTimerState.PAUSED -> {
                listener.onTick(null, TimerException(TimerErrorTypes.CURRENTLY_PAUSED))
            }
            CurrentTimerState.DESTROYED -> {
                listener.onTick(null, TimerException(TimerErrorTypes.DESTROYED))
            }
            else -> {
                timerCanStart(countDownTime, delayMillis)
            }
        }
    }

    fun stopTimer() {
        val error = if (state == CurrentTimerState.STOPPED) {
            TimerException(TimerErrorTypes.NO_TIMER_RUNNING)
        } else {
            null
        }
        state = CurrentTimerState.STOPPED
        listener.onStop(error)
    }

    fun pauseTimer() {
        if (state == CurrentTimerState.PAUSED) {
            Log.e(TAG, "Already paused, check your code for multiple callers")
        }
        state = CurrentTimerState.PAUSED
    }

    fun continueTimer() {
        if (state == CurrentTimerState.RUNNING) {
            Log.e(TAG, "Already running, check your code for multiple callers")
        }
        state = CurrentTimerState.RUNNING
        listener.onContinue()
    }

    fun destroyTimer() {
        scope.cancel("Timer was now destroyed. Need a new instance to work")
        listener.onDestroy()
        state = CurrentTimerState.DESTROYED
    }

    private fun timerCanStart(countDownTime: Long, delayMillis: Long = 1000) {
        scope.launch {
            state = CurrentTimerState.RUNNING
            var timeLeft = countDownTime

            timerLoop@ while (true) {
                when {
                    timeLeft < 1 -> {
                        state = CurrentTimerState.STOPPED
                        listener.onStop()
                        break@timerLoop
                    }
                    timeLeft > 0 && state == CurrentTimerState.RUNNING -> {
                        listener.onTick(timeLeft)
                        delay(delayMillis)
                        timeLeft -= 1
                    }
                    state == CurrentTimerState.PAUSED -> {
                        listener.onPause(timeLeft)
                    }
                    state == CurrentTimerState.STOPPED -> {
                        listener.onStop()
                        break@timerLoop
                    }
                }
            }
        }
    }
}

