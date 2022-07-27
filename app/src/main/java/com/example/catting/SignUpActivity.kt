package com.example.catting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.catting.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        with(binding){
            signUpButton.setOnClickListener{
                val email = emailText.text.toString()
                val password = pwText.text.toString()
                val password2 = password2.text.toString()
                if(password == password2){
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(this@SignUpActivity,"회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                            intent.putExtra("email", email)
                            intent.putExtra("password", password)
                            setResult(RESULT_OK,intent)
                            finish()
                        } else {
                            Toast.makeText(this@SignUpActivity,"이미 가입된 이메일입니다.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}