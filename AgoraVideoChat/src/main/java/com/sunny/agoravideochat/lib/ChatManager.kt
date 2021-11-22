package com.sunny.agoravideochat.lib

import android.content.Context
import android.util.Log
import com.sunny.agoravideochat.BuildConfig
import io.agora.rtm.RtmClient
import io.agora.rtm.RtmClientListener
import com.sunny.agoravideochat.lib.RtmMessagePool
import io.agora.rtm.RtmMessage
import io.agora.rtm.RtmImageMessage
import io.agora.rtm.RtmFileMessage
import io.agora.rtm.RtmMediaOperationProgress
import com.sunny.agoravideochat.lib.ChatManager
import java.lang.Exception
import java.lang.RuntimeException
import java.util.ArrayList

class ChatManager(private val mContext: Context) {
    var rtmClient: RtmClient? = null
        private set
    private val mListenerList: List<RtmClientListener> = ArrayList()
    private val mMessagePool = RtmMessagePool()
    fun init() {
        val appID = "0c825a804e914943a32b5bac902ae2a6"
        try {
            rtmClient = RtmClient.createInstance(mContext, appID, object : RtmClientListener {
                override fun onConnectionStateChanged(state: Int, reason: Int) {
                    for (listener in mListenerList) {
                        listener.onConnectionStateChanged(state, reason)
                    }
                }

                override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String) {}
                override fun onImageMessageReceivedFromPeer(
                    rtmImageMessage: RtmImageMessage,
                    peerId: String
                ) {
                }

                override fun onFileMessageReceivedFromPeer(
                    rtmFileMessage: RtmFileMessage,
                    s: String
                ) {
                }

                override fun onMediaUploadingProgress(
                    rtmMediaOperationProgress: RtmMediaOperationProgress,
                    l: Long
                ) {
                }

                override fun onMediaDownloadingProgress(
                    rtmMediaOperationProgress: RtmMediaOperationProgress,
                    l: Long
                ) {
                }

                override fun onTokenExpired() {}
                override fun onPeersOnlineStatusChanged(status: Map<String, Int>) {}
            })
            if (BuildConfig.DEBUG) {
//                mRtmClient.setParameters("{\"rtm.log_filter\": 21173}");
//                mRtmClient.setParameters("{\"rtm.log_filter\": 21168}");
            }
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw RuntimeException("""
    NEED TO check rtm sdk init fatal error
    ${Log.getStackTraceString(e)}
    """.trimIndent())
        }

        // Global option, mainly used to determine whether
        // to support offline messages now.
    }

    companion object {
        private val TAG = ChatManager::class.java.simpleName
    }
}