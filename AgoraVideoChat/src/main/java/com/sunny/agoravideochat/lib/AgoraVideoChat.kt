package com.sunny.agoravideochat.lib

import android.content.Context
import android.content.Intent
import com.sunny.agoravideochat.models.MeetingInfo
import com.sunny.agoravideochat.service.AgoraVideoChatNotificationService
import com.sunny.agoravideochat.utils.Constants

class AgoraVideoChat {

    private lateinit var meetingInfo: MeetingInfo
    private lateinit var context: Context

    /**
     * Initialize the AgoraVideoChat library
     * @param channelName - Channel name of the meeting
     * @param uid - User id of the user
     * @param context - Context of the application
     * @param isLearner - Is the user a learner or a teacher
     * @param name - Name of the user
     * @param sessionEndTimeInUTC - Session end time in UTC
     * @param channelNameOfRTC - Channel name of the Real time messaging
     */
    fun init(
        appId: String,
        channelName: String,
        uid: String,
        token: String?,
        context: Context,
        isLearner: Boolean,
        name: String,
        sessionEndTimeInUTC: Long,
        channelNameOfRTC: String,
    ) {
        meetingInfo = MeetingInfo(
            appID = appId,
            channelName = channelName,
            userID = uid,
            token = token,
            isLearner = isLearner,
            name = name,
            sessionEndTimeInUTC = sessionEndTimeInUTC,
            channelNameOfRTC = channelNameOfRTC
        )
        this.context = context
    }

    fun startVideoChat() {
        if (this::meetingInfo.isInitialized) {

            // start PreviewActivity passing MeetingInfo
            val intent: Intent = Intent(context,
                AgoraVideoChatNotificationService::class.java)
                intent.putExtra(Constants.MEETING_INFO, meetingInfo)
            context.startService(intent)
//            Intent(context, AgoraVideoChatActivity::class.java).apply {
//
//                context.startActivity(this)
//            }

        } else {
            throw Exception("AgoraVideoChat not initialized")
        }

    }

    companion object {
        const val TAG = "AgoraVideoChat"
    }
}