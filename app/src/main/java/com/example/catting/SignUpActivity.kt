package com.example.catting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.catting.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            signUpButton.setOnClickListener{
                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                intent.putExtra("email",emailText.text.toString())
                intent.putExtra("passWord",pwText.text.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}