package com.sunny.agoravideochat.lib

interface Callback {
    fun onUserLeave()
    fun onUserJoin()
    fun onMeetingEnd()
    fun onMeetingStart()
    fun onMeetingError(error: String)

}