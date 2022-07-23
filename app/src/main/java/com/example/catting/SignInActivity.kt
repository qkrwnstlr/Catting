package com.example.catting

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.catting.databinding.ActivitySignInBinding
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {
    val binding by lazy { ActivitySignInBinding.inflate(layoutInflater) }
    lateinit var socket : Socket
    lateinit var signUpResult: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        socket = SocketApplication.get()
        socket.connect()

        signUpResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                val data: Intent? = it.data
                val email = data?.getStringExtra("email")
                val passWord = data?.getStringExtra("passWord")
                with(binding){
                    emailText.setText(email)
                    pwText.setText(passWord)
                }
            }
        }

        with(binding){
            signUpText.setOnClickListener{
                val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                signUpResult.launch(intent)
            }
            signInButton.setOnClickListener{
                val jsonObject = JSONObject()
                jsonObject.put("email",emailText.text.toString())
                jsonObject.put("passWard",pwText.text.toString())
                socket.emit("sign in request", jsonObject)
                socket.on("sign in result", signInResult)
            }
        }
    }

    val signInResult = Emitter.Listener { args ->
        val obj = JSONObject(args[0].toString())
        val uid = obj.get("uid").toString()
        val nickName = obj.get("nickName").toString()
        val camID = obj.get("camID").toString()
        val catsObj = obj.optJSONArray("cats")
        val cats = arrayListOf<CatProfile>()
        for(i in 0..catsObj!!.length()){
            val catObj = catsObj.getJSONObject(i)
            val cid = catObj.get("cid").toString()
            val cName = catObj.get("cName").toString()
            val cPicture = catObj.get("cPictures").toString()
            cats.plus(CatProfile(cid,cName,cPicture))
        }
        val userInfo = UserInfo(uid, nickName, camID, cats)

        val intent = Intent(this@SignInActivity, SignInActivity::class.java)
        intent.putExtra("userInfo", userInfo)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}