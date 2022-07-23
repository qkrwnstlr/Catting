package com.example.catting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.catting.databinding.ActivitySignInBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {
    val binding by lazy { ActivitySignInBinding.inflate(layoutInflater) }
    lateinit var socket : Socket
    lateinit var signUpResult: ActivityResultLauncher<Intent>

    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("SignInFragment","onCreate")
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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
                val email = emailText.text.toString()
                val password = pwText.text.toString()
                val cats = arrayListOf<CatProfile>()

                //test
                cats.add(CatProfile("1","1","1"))
                cats.add(CatProfile("2","2","2"))
                cats.add(CatProfile("3","3","3"))
                cats.add(CatProfile("4","4","4"))
                Log.d("SignInFragment",cats.toString())
                val userInfo = UserInfo("user","user","user",cats)
                val intent = Intent(this@SignInActivity, SignInActivity::class.java)
                intent.putExtra("userInfo", userInfo)
                setResult(RESULT_OK, intent)
                finish()
                //

                /*auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        Snackbar.make(toolbar,"환영합니다.",Snackbar.LENGTH_LONG).show()
                        socket = SocketApplication.get()
                        socket.connect()
                        val jsonObject = JSONObject()
                        jsonObject.put("email",email)
                        /*socket.emit("sign in request", jsonObject)
                        socket.on("sign in result", signInResult)*/

                        // test
                        val cats = arrayListOf<CatProfile>()
                        cats.plus(CatProfile("1","1","1"))
                        cats.plus(CatProfile("2","2","2"))
                        cats.plus(CatProfile("3","3","3"))
                        cats.plus(CatProfile("4","4","4"))
                        Log.d("SignInFragment",cats.toString())
                        val userInfo = UserInfo("user","user","user",cats)
                        //

                        val intent = Intent(this@SignInActivity, SignInActivity::class.java)
                        intent.putExtra("userInfo", userInfo)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    else{
                        Snackbar.make(toolbar,"아이디와 비밀번호를 다시 확인해주세요.",Snackbar.LENGTH_LONG).show()
                    }
                }*/
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