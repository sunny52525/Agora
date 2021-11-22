package com.sunny.agoravideochat.ui.fragment

//import binding
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sunny.agoravideochat.R
import com.sunny.agoravideochat.models.Layout
import com.sunny.agoravideochat.utils.appendZero
import com.sunny.agoravideochat.utils.setBottomMargin
import com.sunny.agoravideochat.utils.startBlinking
import com.sunny.agoravideochat.viewmodel.VideoChatViewModel
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import org.w3c.dom.Text


class VideoChatFragment : Fragment(R.layout.fragment_video_chat) {

    private var mRtcEngine: RtcEngine? = null

    private lateinit var surfaceViewLocal: SurfaceView
    private lateinit var viewModel: VideoChatViewModel
    private lateinit var countDownTimer: CountDownTimer


    private lateinit var timerCard: CardView
    private lateinit var localViewLayout2: CardView
    private lateinit var remoteViewLayout: RelativeLayout
    private lateinit var remoteView: FrameLayout
    private lateinit var remoteViewMute: TextView
    private lateinit var speakerName: TextView
    private lateinit var timer2card: CardView
    private lateinit var timer2: TextView
    private lateinit var newMessage: CardView
    private lateinit var chatOpen: CardView
    private lateinit var cameraOff: RelativeLayout
    private lateinit var cameraToggle: CardView
    private lateinit var cam: ImageView
    private lateinit var localView: FrameLayout
    private lateinit var timer: TextView
    private lateinit var localViewMute: TextView

    fun initViews(view: View) {
        with(view) {
            cameraOff = findViewById(R.id.camera_off)
            timer = findViewById(R.id.timer)

            timerCard = findViewById(R.id.timer_card)
            localViewLayout2 = findViewById(R.id.local_view_layout2)
            remoteViewLayout = findViewById(R.id.remote_view_layout)
            remoteView = findViewById(R.id.remote_view)
            remoteViewMute = findViewById(R.id.remote_view_mute)
            speakerName = findViewById(R.id.speaker_name)
            timer2card = findViewById(R.id.timer2card)
            timer2 = findViewById(R.id.timer2)
            newMessage = findViewById(R.id.new_message)
            chatOpen = findViewById(R.id.chat_open)
            cameraToggle = findViewById(R.id.camera_toggle)
            cam = findViewById(R.id.cam)
            localView = findViewById(R.id.local_view)
            localViewMute = findViewById(R.id.local_view_mute)
        }
    }


    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        /**
         * Occurs when a remote user (Communication)/ host (Live Broadcast) joins the channel.
         * This callback is triggered in either of the following scenarios:
         *
         * A remote user/host joins the channel by calling the joinChannel method.
         * A remote user switches the user role to the host by calling the setClientRole method after joining the channel.
         * A remote user/host rejoins the channel after a network interruption.
         * The host injects an online media stream into the channel by calling the addInjectStreamUrl method.
         *
         * @param uid User ID of the remote user sending the video streams.
         * @param elapsed Time elapsed (ms) from the local user calling the joinChannel method until this callback is triggered.
         */
        override fun onUserJoined(uid: Int, elapsed: Int) {

            requireActivity().runOnUiThread { setupRemoteVideo(uid) }
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
         *     goodbye message. When this message is received, the SDK determines that the
         *     user/host leaves the channel.
         *
         *     Drop offline: When no data packet of the user or host is received for a certain
         *     period of time (20 seconds for the communication profile, and more for the live
         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
         *     network connection may lead to false detections, so we recommend using the
         *     Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        override fun onUserOffline(uid: Int, reason: Int) {
            requireActivity().runOnUiThread { onRemoteUserLeft() }
        }

        /**
         * Occurs when a remote user stops/resumes sending the video stream.
         *
         * @param uid ID of the remote user.
         * @param muted Whether the remote user's video stream playback pauses/resumes:
         * true: Pause.
         * false: Resume.
         */
        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            requireActivity().runOnUiThread { onRemoteUserVideoMuted(uid, muted) }
        }
    }

    private fun onRemoteUserVideoMuted(uid: Int, muted: Boolean) {


        remoteView.isVisible = muted.not()
        remoteViewMute.isVisible = muted

    }


    private fun onLocalVideoMuted(muted: Boolean) {

        mRtcEngine?.muteLocalVideoStream(muted)
        localView.isVisible = muted.not()
        localViewMute.isVisible = muted
        if (viewModel.layout.value == Layout.FULL_SCREEN)
            cameraOff.isVisible = muted


    }


    private fun onRemoteUserLeft() {
        remoteViewMute.text = getString(R.string.teacher_not_joined)
        remoteViewMute.isVisible = true
        remoteView.isVisible = false


    }

    private fun setupRemoteVideo(uid: Int) {

        viewModel.remoteUserId = uid
        Log.d(TAG, "setupRemoteVideo: $uid")
        remoteViewMute.isVisible = false
        remoteViewMute.text = getString(R.string.camera_off)
        remoteView.isVisible = true
        val surfaceView = RtcEngine.CreateRendererView(requireActivity().baseContext)
        remoteView.addView(surfaceView)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
        surfaceView.tag = uid // for mark purpose

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(VideoChatViewModel::class.java)
        initViews(view)
        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO,
                PERMISSION_REQ_ID_RECORD_AUDIO
            ) && checkSelfPermission(
                Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA
            )
        ) {
            initAgoraEngineAndJoinChannel()
        }

        setObservers()

        viewModel.meetingInfo?.sessionEndTimeInUTC?.let { startReverseTimer(it) }

        cameraToggle.setOnClickListener {
            if (mRtcEngine == null) {
                return@setOnClickListener
            }
            mRtcEngine?.switchCamera()
        }
        chatOpen.setOnClickListener {
            openFragment(ChatFragment())

        }


    }

    fun openFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container_view, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun setObservers() {
        viewModel.cameraOn.observe(viewLifecycleOwner) { cameraOn ->
            onLocalVideoMuted(cameraOn.not())
        }
        viewModel.micOn.observe(viewLifecycleOwner) {
            mRtcEngine?.muteLocalAudioStream(it.not())
        }

        viewModel.userLeft.observe(viewLifecycleOwner) {
            if (it)
                mRtcEngine?.leaveChannel()
        }

        viewModel.layout.observe(viewLifecycleOwner) {
            when (it) {
                Layout.FIFTY_FIFTY -> {
                    cameraOff.isVisible = false
                    val params = remoteViewLayout.layoutParams
                    params.height = 0
                    remoteViewLayout.layoutParams = params

                    localViewLayout2.isVisible = false
                    timerCard.isVisible = true

                    val paramsTimer = timer2card.layoutParams
                    paramsTimer.height = 0
                    timer2card.layoutParams = paramsTimer


                    timer2card.setBottomMargin(R.dimen.zero_dp)

                    if (this::surfaceViewLocal.isInitialized) {
                        localView.removeAllViews()

                        localViewLayout2.removeAllViews()
                        localView.addView(surfaceViewLocal)


                    }

                }
                Layout.FULL_SCREEN -> {
                    cameraOff.isVisible = viewModel.cameraOn.value == false
                    val params = remoteViewLayout.layoutParams
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT

                    remoteViewLayout.layoutParams = params

                    val paramsTimer = timer2card.layoutParams
                    paramsTimer.height = ViewGroup.LayoutParams.WRAP_CONTENT

                    timer2card.layoutParams = paramsTimer

                    timer2card.setBottomMargin(R.dimen.twenty_dp)


                    localViewLayout2.isVisible = true
                    timerCard.isVisible = false




                    if (this::surfaceViewLocal.isInitialized) {

                        localView.removeAllViews()
                        localViewLayout2.removeAllViews()
                        localViewLayout2.addView(surfaceViewLocal)

                    }
                }
                else -> {

                }
            }
        }
        viewModel.newMessage.observe(viewLifecycleOwner) {
            newMessage.isVisible = it
        }

//        speakerName.text = viewModel.meetingInfo?.callerName
        // TODO: Add observer for meeting info

    }


    private fun initAgoraEngineAndJoinChannel() {


        initializeAgoraEngine()
        setupVideoProfile()
        setupLocalVideo()
        joinChannel()
    }

    private fun initializeAgoraEngine() {

        viewModel.meetingInfo?.appID?.let { appID ->
            try {
                mRtcEngine =
                    RtcEngine.create(requireActivity().baseContext, appID, mRtcEventHandler)
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))

                throw RuntimeException(
                    "NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(
                        e
                    )
                )
            }
        }
        if (viewModel.frontCamera.value == false) {
            mRtcEngine?.switchCamera()
        }

    }

    private fun setupVideoProfile() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine!!.enableVideo()

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine!!.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

    private fun setupLocalVideo() {

        surfaceViewLocal = RtcEngine.CreateRendererView(requireActivity().baseContext)
        surfaceViewLocal.setZOrderMediaOverlay(true)
        localView.addView(surfaceViewLocal)
        mRtcEngine!!.setupLocalVideo(VideoCanvas(surfaceViewLocal, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun joinChannel() {


        val token: String? = viewModel.meetingInfo?.token

        mRtcEngine!!.joinChannel(
            token,
            viewModel.meetingInfo?.channelName,
            "Clapingo Meet",
            0
        ) // if you do not specify the uid, we will generate the uid for you
    }


    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i(TAG, "checkSelfPermission $permission $requestCode")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    //set binding null on destroy
    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
        mRtcEngine = null

        if (this::countDownTimer.isInitialized)
            countDownTimer.cancel()

    }

    private fun leaveChannel() {
        mRtcEngine?.leaveChannel()
    }

    // start reverse timer from current time to end time
    private fun startReverseTimer(endTime: Long) {

        val startTime = System.currentTimeMillis()
        val timeLeft = endTime - startTime
        countDownTimer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                timer.text = "$minutes:${remainingSeconds.appendZero()}"
                timer2.text = "$minutes:${remainingSeconds.appendZero()}"
                if (minutes == 2L && remainingSeconds == 59L) {
                    try {
                        timerCard.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )


                    } catch (e: Exception) {

                    }

                    if (viewModel.layout.value == Layout.FIFTY_FIFTY)
                        startBlinking(timerCard)
                    else
                        startBlinking(timer2card)
                }

            }

            override fun onFinish() {
                timer.text = "00:00"
                requireActivity().finish()
            }
        }
        countDownTimer.start()


    }


    companion object {
        private const val TAG = "VideoChatFragment"
        private const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
        private const val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    }

}