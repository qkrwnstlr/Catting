package com.example.catting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.catting.databinding.*
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class ChattingActivity : AppCompatActivity() {
    val binding by lazy { ActivityChattingBinding.inflate(layoutInflater) }
    lateinit var socket : Socket
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d("NextFragment","onCreate")
        with(binding){
            inputText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(inputText.text.toString()!=""){
                        fileButton.visibility = View.INVISIBLE
                        sendButton.visibility = View.VISIBLE
                    }
                    else{
                        fileButton.visibility = View.VISIBLE
                        sendButton.visibility = View.INVISIBLE
                    }
                }
            })
            plusButton.setOnClickListener{
                plusButton.visibility = View.INVISIBLE
                minusButton.visibility = View.VISIBLE
                optiontLayout.visibility = View.VISIBLE
            }
            minusButton.setOnClickListener{
                plusButton.visibility = View.VISIBLE
                minusButton.visibility = View.INVISIBLE
                optiontLayout.visibility = View.GONE
            }
            fileButton.setOnClickListener{

            }
            sendButton.setOnClickListener{
                socket = MainActivity.getInstance()?.socket!!
                val jsonObject = JSONObject()
                jsonObject.put("email","")
                socket.emit("SendMessage", jsonObject)
                Thread(object : Runnable{
                    override fun run() {
                        runOnUiThread(Runnable {
                            kotlin.run {
                                // 리사이클러뷰에 유저 대사로 새로운 메시지 추가
                            }
                        })
                    }
                }).start()
                socket.on("SendMessage", sendMessage)
            }
        }
    }

    var sendMessage = Emitter.Listener { args ->
        val obj = JSONObject(args[0].toString())
        Thread(object : Runnable{
            override fun run() {
                runOnUiThread(Runnable {
                    kotlin.run {
                        // 리사이클러뷰에 고양이 대사로 새로운 메시지 추가
                    }
                })
            }
        }).start()
    }

    override fun onPause() {
        super.onPause()
        Log.d("NextFragment","onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NextFragment","onDestroy")
    }

}