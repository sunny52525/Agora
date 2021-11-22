package com.sunny.agoravideochat.ui.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.sunny.agoravideochat.R
import com.sunny.agoravideochat.lib.ChatManager
import com.sunny.agoravideochat.models.ChatMessage
import com.sunny.agoravideochat.models.Layout
import com.sunny.agoravideochat.models.MeetingInfo
import com.sunny.agoravideochat.models.MessageType
import com.sunny.agoravideochat.ui.fragment.ChatFragment
import com.sunny.agoravideochat.ui.fragment.VideoChatFragment
import com.sunny.agoravideochat.utils.Constants
import com.sunny.agoravideochat.viewmodel.VideoChatViewModel
import io.agora.rtm.*


class AgoraVideoChatActivity : AppCompatActivity() {
    private lateinit var viewModel: VideoChatViewModel

    private lateinit var mainLayout: CoordinatorLayout
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var cameraToggle: MaterialCardView
    private lateinit var videoCard: CardView
    private lateinit var cam: ImageView
    private lateinit var micToggle: MaterialCardView
    private lateinit var micCard: CardView
    private lateinit var mic: ImageView
    private lateinit var moreOptions: ImageView
    private lateinit var leave: FloatingActionButton

    fun initViews() {
        bottomAppBar = findViewById(R.id.bottom_app_bar)
        cameraToggle = findViewById(R.id.camera_toggle)
        videoCard = findViewById(R.id.video_card)
        cam = findViewById(R.id.cam)
        micToggle = findViewById(R.id.mic_toggle)
        micCard = findViewById(R.id.mic_card)
        mic = findViewById(R.id.mic)
        moreOptions = findViewById(R.id.more_options)
        leave = findViewById(R.id.leave)
    }

    val rtmChannelListener = object : RtmChannelListener {
        override fun onMemberCountUpdated(p0: Int) {
        }

        override fun onAttributesUpdated(p0: MutableList<RtmChannelAttribute>?) {
        }

        override fun onMessageReceived(message: RtmMessage?, fromMember: RtmChannelMember?) {
            runOnUiThread {
                viewModel.newMessage.postValue(true)
                viewModel.addMessage(
                    ChatMessage(
                        message = message?.text.toString(),
                        messageType = MessageType.RECEIVED
                    )
                )
                val text = message?.text
                val fromUser = fromMember?.userId
                Log.d(TAG, "onMessageReceived: $text")
                Log.d(TAG, "onMessageReceived: $fromUser")
                val account = fromMember?.userId
                Log.i(TAG, "onMessageReceived account = $account msg = $message")

                if (viewModel.isChatOpen.value == false) {
                    val snackbar = showSnackbar(mainLayout, Snackbar.LENGTH_LONG)

                    snackbar.show()

                    val view = snackbar.view

                    val tv: TextView = view.findViewById<View>(R.id.reply) as TextView
                    val speakerName = view.findViewById<TextView>(R.id.speaker_name)
                    val newMessage = view.findViewById<TextView>(R.id.message)
                    speakerName.text = viewModel.meetingInfo?.name
                    newMessage.text = message?.text
                    tv.setOnClickListener {
                        openFragment(ChatFragment(), replace = false)
                        snackbar.dismiss()
                    }
                }

            }

        }

        override fun onImageMessageReceived(p0: RtmImageMessage?, p1: RtmChannelMember?) {
        }

        override fun onFileMessageReceived(p0: RtmFileMessage?, p1: RtmChannelMember?) {
        }

        override fun onMemberJoined(p0: RtmChannelMember?) {
        }

        override fun onMemberLeft(p0: RtmChannelMember?) {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora_video_chat)

        viewModel = ViewModelProvider(this).get(VideoChatViewModel::class.java)

        initViews()

        val meetingInfo = intent.getParcelableExtra<MeetingInfo>(Constants.MEETING_INFO)
        viewModel.meetingInfo = meetingInfo

        viewModel.micPermissionGranted.postValue(hasAudioPermission())
        viewModel.cameraPermissionGranted.postValue(hasCameraPermission())
        viewModel.cameraOn.postValue(hasCameraPermission())


        if (allPermissionsGranted().not()) {

            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )

        }


        val filter = IntentFilter()
        filter.addAction("com.app.clapingo.endmeet")


        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                finish()
            }
        }
        registerReceiver(
            receiver, filter
        )


        setListeners()
        setObservers()
        openFragment(VideoChatFragment())
        initChat()


    }

    private fun setObservers() {
        viewModel.micOn.observe(this) { micOn ->
            if (micOn) {

                mic.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mic_on))
                micCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))


            } else {
                mic.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.mic_off_icon
                    )
                )
                micCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.primary
                    )
                )

            }

        }
        viewModel.cameraOn.observe(this) { cameraOn ->
            if (cameraOn) {
                cam.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.camera_on))
                videoCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )

            } else {
                cam.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_video))
                videoCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.primary
                    )
                )

            }
        }

        viewModel.isChatOpen.observe(this) {
            bottomAppBar.isVisible = it.not()
            leave.isVisible = it.not()
        }
    }

    private fun setListeners() {
        micToggle.setOnClickListener {
            viewModel.toggleMic()
        }
        cameraToggle.setOnClickListener {
            viewModel.toggleCamera()
        }

        leave.setOnClickListener {
            onBackPressed()
            viewModel.leaveMeeting()
        }


        moreOptions.setOnClickListener {
            val build = MaterialAlertDialogBuilder(this, R.style.WrapContentDialog)
            val viewGroup = findViewById<CoordinatorLayout>(R.id.mainLayout)
            val dialogView = LayoutInflater.from(this.applicationContext)

                .inflate(R.layout.layout_chooser, viewGroup, false)

            val half = dialogView.findViewById<CardView>(R.id.half)
            val full = dialogView.findViewById<CardView>(R.id.full)


            build.setView(dialogView)

            val alertDialog = build.create()
            val window: Window? = alertDialog.window
            val wlp: WindowManager.LayoutParams? = window?.attributes


            wlp?.gravity = Gravity.END + Gravity.BOTTOM
            wlp?.flags = wlp?.flags?.and(WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv())
            window?.attributes = wlp

            viewModel.layout.value?.let {
                if (it == Layout.FIFTY_FIFTY) {
                    half.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    full.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
                } else {
                    half.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
                    full.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                }
            }

            half.setOnClickListener {
                alertDialog.dismiss()
                viewModel.layout.postValue(Layout.FIFTY_FIFTY)
            }
            full.setOnClickListener {
                alertDialog.dismiss()
                viewModel.layout.postValue(Layout.FULL_SCREEN)
            }

            alertDialog.show()

        }
    }

    private fun showSnackbar(
        coordinatorLayout: CoordinatorLayout,
        duration: Int,
    ): Snackbar {
        val snackbar = Snackbar.make(coordinatorLayout, "", duration)

        val height = 100f


        val snackView: View = layoutInflater.inflate(R.layout.new_message_layout, null)

        snackbar.view.setBackgroundColor(Color.WHITE)
        snackbar.view.background = ContextCompat.getDrawable(this, R.drawable.round_edge)
        val snackBarView = snackbar.view as SnackbarLayout
        val parentParams =
            snackBarView.layoutParams
        parentParams.height = height.toInt()
        parentParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        snackBarView.layoutParams = parentParams
        snackBarView.addView(snackView, 0)
        return snackbar
    }

    private fun initChat() {

        val mChatManager = ChatManager(application)
        mChatManager.init()


        viewModel.rtmClient = mChatManager.rtmClient

        viewModel.rtmClient?.let { rtmClient ->
            rtmClient.login(null, viewModel.meetingInfo?.name, object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    runOnUiThread {
                        Toast.makeText(
                            this@AgoraVideoChatActivity,
                            "Login success",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        Log.i(TAG, "onSuccess: ")


                        viewModel.channel =
                            rtmClient.createChannel(
                                viewModel.meetingInfo?.channelNameOfRTC,
                                rtmChannelListener
                            )

                        if (viewModel.channel == null) {
                            Toast.makeText(
                                this@AgoraVideoChatActivity,
                                "Join channel failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        viewModel.channel?.join(object : ResultCallback<Void> {
                            override fun onSuccess(p0: Void?) {
                                viewModel.channel?.join(object : ResultCallback<Void> {
                                    override fun onSuccess(p0: Void?) {
                                        Log.d(TAG, " : ")
                                    }

                                    override fun onFailure(p0: ErrorInfo?) {
                                        Log.d(TAG, "onFailure: ${p0?.errorDescription}")
                                    }

                                })

                            }

                            override fun onFailure(p0: ErrorInfo?) {
                                runOnUiThread {
                                    Log.d(TAG, "onFailure ${p0?.errorDescription}: ")

                                }
                            }

                        })
                    }
                }

                override fun onFailure(p0: ErrorInfo?) {
                    Toast.makeText(this@AgoraVideoChatActivity, "Login Failed", Toast.LENGTH_SHORT)
                        .show()

                    Log.d(TAG, "onFailure: ${p0?.errorDescription} ")
                }

            })



            viewModel.userLeft.observe(this) {
                if (it)
                    rtmClient.logout(null)
            }
        }


    }


    private fun openFragment(fragment: Fragment, replace: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        if (replace)
            transaction.replace(R.id.fragment_container_view, fragment)
        else {
            transaction.add(R.id.fragment_container_view, fragment)
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }


    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            viewModel.micPermissionGranted.postValue(hasAudioPermission())
            viewModel.cameraPermissionGranted.postValue(hasCameraPermission())

            if (allPermissionsGranted().not()) {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        private const val TAG = "PreviewActivity"

    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.isChatOpen.postValue(false)

    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationManagerCompat.from(this).cancelAll();
        viewModel.leaveMeeting()
    }


}
