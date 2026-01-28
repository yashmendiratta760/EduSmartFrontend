package com.yash.edusmart.data

data class ChatMessage(
    val sender: String,
    val receiver: String,
    val message: String,
    val messageType : MessageType
)

enum class MessageType{
    JOIN,
    LEAVE,
    CHAT
}
