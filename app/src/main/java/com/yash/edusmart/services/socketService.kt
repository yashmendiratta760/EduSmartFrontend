package com.yash.edusmart.services

import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.yash.edusmart.data.AssignmentDTO
import com.yash.edusmart.data.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribe
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

fun isEmulator(): Boolean {
    return Build.FINGERPRINT.contains("generic")
            || Build.MODEL.contains("Emulator")
            || Build.PRODUCT.contains("sdk")
}

fun getWebSocketUrl(): String {
    return "https://edusmartbackend-z95q.onrender.com/ws/websocket"
//    return if (isEmulator()) "http://10.0.2.2:8080/ws/websocket" else "http://192.168.0.102:8080/ws/websocket"
}



object SocketService {

    private val WS_URL = getWebSocketUrl()

    private val stompClient = StompClient(OkHttpWebSocketClient())
    private var session: StompSession? = null

    private val socketScopePr =
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val socketScopePu =
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    @Volatile
    private var isConnected = false

    suspend fun connect(jwt: String) {
        if (isConnected) return
        if (session != null) return

        session = stompClient.connect(
            WS_URL,
            customStompConnectHeaders = mapOf(
                "Authorization" to "Bearer $jwt"
            )
        )
        isConnected=true

        Log.d("STOMP", "CONNECTED")
    }


    fun subscribePrivate(onMessage: (ChatMessage) -> Unit) {
        socketScopePr.launch {
            session!!
                .subscribe("/user/queue/messages")
                .collect { frame ->
                    val msg = Gson().fromJson(
                        frame.bodyAsText,
                        ChatMessage::class.java
                    )
                    Log.d("STOMP", "PRIVATE MSG = ${msg.message}")
                    onMessage(msg)
                }
        }
    }

    fun subscribeGroup(onMessage: (ChatMessage) -> Unit,groupId: String){
        socketScopePu.launch {
            session!!.subscribe("/topic/group.receiveMessage/${groupId}")
                .collect { frame->
                    val msg = Gson().fromJson(
                        frame.bodyAsText,
                        ChatMessage::class.java
                    )
                    onMessage(msg)
                }
        }
    }

    fun sendGroup(message: ChatMessage,groupId: String){
        socketScopePu.launch {
            if (!isConnected || session == null) {
                Log.e("STOMP_CLIENT", "❌ SEND blocked: not connected yet")
                return@launch
            }
            val json = Gson().toJson(message)

            session!!.sendText(
                destination = "/app/group.sendMessage/${groupId}",
                body = json
            )
        }
    }


    fun subscribeAssignment(onMessage: (AssignmentDTO) -> Unit, groupId: String){
        socketScopePu.launch {
            session!!.subscribe("/topic/assignment.receive/${groupId}")
                .collect { frame->
                    val msg = Gson().fromJson(
                        frame.bodyAsText,
                        AssignmentDTO::class.java
                    )
                    onMessage(msg)
                }
        }
    }

    fun sendAssignment(message: AssignmentDTO,groupId: String){
        socketScopePu.launch {
            if (!isConnected || session == null) {
                Log.e("STOMP_CLIENT", "❌ SEND blocked: not connected yet")
                return@launch
            }
            val json = Gson().toJson(message)

            session!!.sendText(
                destination = "/app/assignment.send/${groupId}",
                body = json
            )
        }
    }


    fun sendPrivate(message: ChatMessage) {
        socketScopePr.launch {
            if (!isConnected || session == null) {
                Log.e("STOMP_CLIENT", "❌ SEND blocked: not connected yet")
                return@launch
            }

            Log.d("STOMP_CLIENT", "🔥 SEND EXECUTING: ${message.message}")

            val json = Gson().toJson(message)

            session!!.sendText(
                destination = "/app/chat.sendPrivateMessage",
                body = json
            )
        }
    }

    fun disconnect() {
        socketScopePr.launch {
            session?.disconnect()
            session = null
        }
    }
}
