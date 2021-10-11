package uz.texnopos.test.timer

class TimerException(val type: TimerErrorTypes) : Exception(type.message)