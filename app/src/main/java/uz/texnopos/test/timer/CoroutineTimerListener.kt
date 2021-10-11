package uz.texnopos.test.timer

interface CoroutineTimerListener {
    fun onTick(timeLeft: Long?, error: Exception? = null)
    fun onStop(error: Exception? = null) {}
    fun onContinue() {}
    fun onPause(remainingTime: Long) {}
    fun onDestroy() {}
}