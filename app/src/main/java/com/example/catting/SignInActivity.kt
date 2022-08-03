package com.example.catting

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.ListFragment
import androidx.room.Room
import com.example.catting.databinding.ActivitySignInBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    val binding by lazy { ActivitySignInBinding.inflate(layoutInflater) }
    //lateinit var socket : Socket
    lateinit var signUpResult: ActivityResultLauncher<Intent>
    lateinit var api: RetrofitApplication
    lateinit var auth:FirebaseAuth

    lateinit var signInInfoHelper: SignInInfoHelper
    var signInInfoList = listOf<SignInInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        signInInfoHelper = Room.databaseBuilder(this,SignInInfoHelper::class.java,"sign_in_info_db")
            .build()
        getSignInInfo()

        auth = FirebaseAuth.getInstance()

        signUpResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                val data: Intent? = it.data
                val email = data?.getStringExtra("email")
                val password = data?.getStringExtra("password")
                with(binding){
                    emailText.setText(email)
                    pwText.setText(password)
                }
            }
        }

        with(binding){
            signUpText.setOnClickListener{
                val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                signUpResult.launch(intent)
            }
            signInButton.setOnClickListener{
                val email = emailText.text.toString()
                val password = pwText.text.toString()

                if(email.isNotEmpty() && password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            CoroutineScope(Dispatchers.IO).launch {
                                signInInfoHelper.signInInfoDao().updateSignInInfo(SignInInfo(email, password))
                            }
                            Toast.makeText(this@SignInActivity, "환영합니다.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@SignInActivity, SignInActivity::class.java)

                            //test
                            setResult(RESULT_FIRST_USER, intent)
                            finish()
                            //

                            /*api = MainActivity.getInstance()?.api!!
                            api.getUserInfo(it.result.user!!.uid).enqueue(object: Callback<UserInfo> {
                                override fun onResponse(
                                    call: Call<UserInfo>,
                                    response: Response<UserInfo>
                                ) {
                                    val body = response.body().toString()
                                    if(body.isNotEmpty()){
                                        val userInfo = Gson().fromJson(body, UserInfo::class.java)
                                        intent.putExtra("userInfo", userInfo)
                                        setResult(RESULT_OK, intent)
                                        finish()
                                    }
                                    else {
                                        setResult(RESULT_FIRST_USER, intent)
                                        finish()
                                        //UserInfo Fragment 실행 -> UserInfo Fragment 는 api.sendUserInfo 로 새로운 유저 db에 저장
                                    }
                                }

                                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                                    Log.d("SignInActivity",t.message.toString())
                                    Log.d("SignInActivity","fail")
                                }
                            })*/
                            /*socket = MainActivity.getInstance()?.socket!!
                            val jsonObject = JSONObject()
                            jsonObject.put("email",email)
                            socket.emit("sign in request", jsonObject)
                            socket.on("sign in result", signInResult)*/
                        } else {
                            Toast.makeText(
                                this@SignInActivity, "아이디와 비밀번호를 다시 확인해주세요.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    /*val signInResult = Emitter.Listener { args ->
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
    }*/

    fun getSignInInfo(){
        CoroutineScope(Dispatchers.IO).launch {
            signInInfoList = signInInfoHelper.signInInfoDao().getSignInInfo()
            withContext(Dispatchers.Main){
                if(signInInfoList.isNotEmpty()) {
                    binding.emailText.setText(signInInfoList[0].email)
                    binding.pwText.setText(signInInfoList[0].password)
                    binding.signInButton.callOnClick()
                }
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}