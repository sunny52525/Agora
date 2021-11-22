package com.sunny.agoravideochat.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MeetingInfo(
    val appID: String,
    val channelName: String,
    val userID: String,
    val token: String?,
    val isLearner: Boolean,
    val name: String,
    val sessionEndTimeInUTC: Long,
    val channelNameOfRTC: String,
) : Parcelable