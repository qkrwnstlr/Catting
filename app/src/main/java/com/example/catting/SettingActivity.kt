package com.example.catting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.room.Room
import com.example.catting.databinding.ActivitySettingBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingActivity : AppCompatActivity() {
    val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }
    private val actionBar get() = supportActionBar!!
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        auth = FirebaseAuth.getInstance()

        //Toolbar에 표시되는 제목의 표시 유무를 설정. false로 해야 custom한 툴바의 이름이 화면에 보인다.
        actionBar.setDisplayShowTitleEnabled(false)
        //왼쪽 버튼 사용설정(기본은 뒤로가기)
        actionBar.setDisplayHomeAsUpEnabled(true)
        binding.logoutButton.setOnClickListener{
            val mainActivity = MainActivity.getInstance()
            val signInInfoHelper = Room.databaseBuilder(this,SignInInfoHelper::class.java,"sign_in_info_db")
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                signInInfoHelper.signInInfoDao().updateSignInInfo(SignInInfo("",""))
            }
            auth.signOut()
            val intent = Intent(mainActivity,SignInActivity::class.java)
            mainActivity?.signInResult!!.launch(intent)
            finish()
        }
    }
    //item 버튼 메뉴 Toolbar에 집어 넣기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting_toolbar, menu)
        return true
    }

    //item 버튼 클릭 했을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //뒤로가기 버튼 눌렀을 때
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}