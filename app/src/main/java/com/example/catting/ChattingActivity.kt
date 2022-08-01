package com.example.catting

import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.catting.databinding.*
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class ChattingActivity : AppCompatActivity() {
    val binding by lazy { ActivityChattingBinding.inflate(layoutInflater) }
    //lateinit var socket : Socket
    lateinit var api: RetrofitApplication
    lateinit var userInfo: UserInfo
    lateinit var catInfo: CatInfo

    lateinit var chattingAdapter: ChattingAdapter
    var chattingLogList = arrayListOf<ChattingLog>()

    lateinit var imm : InputMethodManager
    lateinit var keyboardRect : Rect

    lateinit var logHelper: ChattingLogHelper

    private val actionBar get() = supportActionBar!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.chatting_orange)

        setSupportActionBar(binding.toolbar)
        //Toolbar에 표시되는 제목의 표시 유무를 설정. false로 해야 custom한 툴바의 이름이 화면에 보인다.
        actionBar.setDisplayShowTitleEnabled(false)
        //왼쪽 버튼 사용설정(기본은 뒤로가기)
        actionBar.setDisplayHomeAsUpEnabled(true)

        val mRootWindow = window
        val mRootView = mRootWindow.decorView.findViewById<View>(android.R.id.content)
        mRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            val view = mRootWindow.decorView
            view.getWindowVisibleDisplayFrame(rect)
            keyboardRect = rect
        }

        catInfo = intent.getLargeExtra<CatInfo>("catInfo")!!
        userInfo = MainActivity.getInstance()?.userInfo!!
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager

        logHelper = Room.databaseBuilder(this,ChattingLogHelper::class.java,"chatting_log_db")
            .build()

        getAllChattingLog()
        chattingAdapter = ChattingAdapter(chattingLogList, catInfo)

        with(binding){
            title.text = catInfo.cName
            chattingRecycler.layoutManager = LinearLayoutManager(this@ChattingActivity,LinearLayoutManager.VERTICAL,false)
            chattingRecycler.adapter = chattingAdapter
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
            openButton.setOnClickListener{
                openButton.visibility = View.INVISIBLE
                closeButton.visibility = View.VISIBLE
                optiontLayout.visibility = View.VISIBLE
                imm.hideSoftInputFromWindow(openButton.windowToken, 0)
            }
            closeButton.setOnClickListener{
                openButton.visibility = View.VISIBLE
                closeButton.visibility = View.INVISIBLE
                optiontLayout.visibility = View.GONE
                imm.showSoftInput(inputText, 0)
            }
            fileButton.setOnClickListener{

            }
            sendButton.setOnClickListener{


                //test
                val body = "{\"uid\" : \"${userInfo.uid}\", \"cat_id\": ${catInfo.cid}, \"catImg\": \"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\" ,\"catAnswer\": \"ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd\"}"
                val userChattingLog = ChattingLog(userInfo.uid, catInfo.cid, inputText.text.toString(),null,null)
                val catChattingLog = Gson().fromJson(body, ChattingLog::class.java)
                Thread {
                    runOnUiThread(Runnable {
                        kotlin.run {
                            insertChattingLog(userChattingLog)
                            chattingAdapter.addItem(userChattingLog)
                            insertChattingLog(catChattingLog)
                            chattingAdapter.addItem(catChattingLog)
                            chattingRecycler.smoothScrollToPosition(chattingAdapter.itemCount)
                        }
                    })
                }.start()
                //


                /*api = MainActivity.getInstance()?.api!!
                val userChattingLog = ChattingLog(userInfo.uid, catInfo.cid,inputText.text.toString(),null,null)
                Thread {
                    runOnUiThread(Runnable {
                        kotlin.run {
                            insertChattingLog(userChattingLog)
                            chattingAdapter.addItem(userChattingLog)
                            chattingRecycler.smoothScrollToPosition(chattingAdapter.itemCount)
                        }
                    })
                }.start()
                api.sendMessage(userChattingLog).enqueue(object: Callback<ChattingLog>{
                    override fun onResponse(
                        call: Call<ChattingLog>,
                        response: Response<ChattingLog>
                    ) {
                        val body = response.body().toString()
                        if(body.isNotEmpty()){
                            val catChattingLog = Gson().fromJson(body, ChattingLog::class.java)
                            Thread {
                                runOnUiThread(Runnable {
                                    kotlin.run {
                                        insertChattingLog(catChattingLog)
                                        chattingAdapter.addItem(catChattingLog)
                                        chattingRecycler.smoothScrollToPosition(chattingAdapter.itemCount)
                                    }
                                })
                            }.start()
                        }
                    }

                    override fun onFailure(call: Call<ChattingLog>, t: Throwable) {
                        Log.d("ChattingActivity",t.message.toString())
                        Log.d("ChattingActivity","fail")
                    }

                })*/
                /*socket = MainActivity.getInstance()?.socket!!
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
                socket.on("SendMessage", sendMessage)*/
                inputText.text = null
            }
        }
    }

    /*var sendMessage = Emitter.Listener { args ->
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
    }*/

    fun getAllChattingLog(){
        CoroutineScope(Dispatchers.IO).launch {
            val chattingLog = logHelper.chattingLogDao().getAll(userInfo.uid, catInfo.cid)
            chattingLogList.addAll(chattingLog as ArrayList<ChattingLog>)
            withContext(Dispatchers.Main){
                chattingAdapter.notifyDataSetChanged()
                binding.chattingRecycler.scrollToPosition(chattingLogList.size - 1)
            }
        }
    }

    fun insertChattingLog(chattingLog: ChattingLog){
        CoroutineScope(Dispatchers.IO).launch {
            logHelper.chattingLogDao().insert(chattingLog)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("NextFragment","onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_white)
        Log.d("NextFragment","onDestroy")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chatting_toolbar, menu)
        return true
    }

    //item 버튼 클릭 했을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //뒤로가기 버튼 눌렀을 때
                Log.d("ToolBar_item: ", "뒤로가기 버튼 클릭")
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

class ChattingAdapter(val items:ArrayList<ChattingLog>, val catProfile: CatInfo) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val CAT_MESSAGE = 0
    private val USER_MESSAGE = 1
    //private val items = ArrayList<MessageLog>()
    inner class UserHolder(val binding: ItemChattingUserBubbleBinding): RecyclerView.ViewHolder(binding.root){
        fun setUserMessageLog(chattingLog: ChattingLog) {
            binding.message.text = chattingLog.userMessage
        }
    }

    inner class CatHolder(val binding: ItemChattingCatBubbleBinding): RecyclerView.ViewHolder(binding.root){
        fun setCatMessageLog(chattingLog: ChattingLog){
            with(binding) {
                val decodeString = Base64.decode(this@ChattingAdapter.catProfile.cPicture, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
                catProfile.setImageBitmap(decodedByte)
                cName.text = this@ChattingAdapter.catProfile.cName
                message.text = chattingLog.catAnswer
                if (chattingLog.image == null) {
                    binding.imageView.visibility = View.GONE
                } else {
                    val decodeString = Base64.decode(chattingLog.image, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
                    binding.imageView.setImageBitmap(decodedByte)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].userMessage){
            null -> CAT_MESSAGE
            else -> USER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CAT_MESSAGE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChattingCatBubbleBinding.inflate(layoutInflater, parent, false)
                CatHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChattingUserBubbleBinding.inflate(layoutInflater, parent, false)
                UserHolder(binding)
            }
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is UserHolder){
            holder.setUserMessageLog(items[position])
        }
        else if(holder is CatHolder){
            holder.setCatMessageLog(items[position])
        }
    }

    fun addItem(chattingLog: ChattingLog){
        items.add(chattingLog)
        notifyDataSetChanged()
    }
}