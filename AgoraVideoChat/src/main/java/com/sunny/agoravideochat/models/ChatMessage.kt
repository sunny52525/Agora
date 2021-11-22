package com.sunny.agoravideochat.models

data class ChatMessage(
    val messageType: MessageType,
    val message: String,
)
