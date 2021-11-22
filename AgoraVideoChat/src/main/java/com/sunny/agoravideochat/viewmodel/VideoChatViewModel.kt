package com.sunny.agoravideochat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunny.agoravideochat.models.ChatMessage
import com.sunny.agoravideochat.models.Layout
import com.sunny.agoravideochat.models.MeetingInfo
import io.agora.rtm.RtmChannel
import io.agora.rtm.RtmClient

class VideoChatViewModel : ViewModel() {


    var channel: RtmChannel? = null
    var rtmClient: RtmClient?=null
    var meetingInfo: MeetingInfo? = null
    val cameraPermissionGranted = MutableLiveData<Boolean>(false)
    val micPermissionGranted = MutableLiveData<Boolean>(false)


    val userLeft = MutableLiveData<Boolean>(false)
    val isChatOpen = MutableLiveData<Boolean>(false)
    var cameraOn = MutableLiveData<Boolean>(false)
    val micOn = MutableLiveData<Boolean>(false)
    var speakerOn = false
    val frontCamera = MutableLiveData<Boolean>(true)

    val layout = MutableLiveData(Layout.FIFTY_FIFTY)

    val newMessage = MutableLiveData(false)

  val messages = MutableLiveData<MutableList<ChatMessage>>()

    var remoteUserId: Int? = 0

    init {
        messages.value = arrayListOf()
    }

    fun toggleLayout() {
        when (layout.value) {
            Layout.FIFTY_FIFTY -> layout.value = Layout.FULL_SCREEN
            Layout.FULL_SCREEN -> layout.value = Layout.FIFTY_FIFTY
        }
    }

    fun toggleFrontCamera() {
        frontCamera.value = !frontCamera.value!!
    }

    fun toggleSpeaker() {
        speakerOn = !speakerOn
    }

    fun toggleCamera() {
        cameraOn.value = !cameraOn.value!!
    }

    fun toggleMic() {
        micOn.value = !micOn.value!!
    }


    fun leaveMeeting() {
        userLeft.value = true
    }

    fun addMessage(chatMessage: ChatMessage) {

        messages += chatMessage

    }
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(item: T) {
    val value = this.value ?: mutableListOf()
    value.add(item)
    this.value = value
}