package uz.texnopos.test.timer

enum class TimerErrorTypes(val message: String) {
    ALREADY_RUNNING("This instance of the timer is already running, create a new instance or stop your current one"),
    CURRENTLY_PAUSED("This timer is currently paused. Choose to continue or stop to start over"),
    NO_TIMER_RUNNING("You are trying to stop or pause a timer that isn't running"),
    DESTROYED("This timer is destroyed and can't be used anymore")
}