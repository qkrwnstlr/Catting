package com.example.catting

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketApplication {
    companion object {
        private lateinit var socket: Socket
        fun get(): Socket {
            try {
                // [uri]부분은 "http://X.X.X.X:3000" 꼴로 넣어주는 게 좋다.
                socket = IO.socket("http://10.0.2.2:3030")
                Log.d("SocketApplication", "success")
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                Log.d("SocketApplication", "error")
            }
            return socket
        }
    }
}