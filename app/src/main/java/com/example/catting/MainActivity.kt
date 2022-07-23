package com.example.catting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.catting.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var socket: Socket
    lateinit var signInResult :ActivityResultLauncher<Intent>
    var userInfo: UserInfo = UserInfo(null,null,null, arrayListOf())
    var mBackWait:Long = 0

    init{
        instance = this
    }

    companion object{
        private var instance:MainActivity? = null
        fun getInstance(): MainActivity? {
            return instance
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        socket = SocketApplication.get()
        socket.connect()

        signInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                val data: Intent? = it.data
                userInfo = data?.getParcelableExtra<UserInfo>("userInfo")!!
                //Log.d("MainFragment", data.getParcelableExtra<UserInfo>("userInfo").toString())
                Log.d("MainFragment","${userInfo.cats}")
            }
        }

        val intent = Intent(this@MainActivity, SignInActivity::class.java)
        //signInResult.launch(intent)

        with(binding) {
            Log.d("MainFragment","onCreate")
            // 1. 페이지 데이터를 로드
            val fragmentList =
                listOf<Fragment>(ChattingFragment(), UserInfoFragment(), FileFragment())
            // 2. 어댑터 생성
            val mainPagerAdapter = MainFragmentPagerAdapter(fragmentList, this@MainActivity)
            // 3. 어댑터와 뷰 페이저 연결
            veiwPager.adapter = mainPagerAdapter
            // 4. 탭 메뉴 생성
            val mainFragmentName = listOf(R.drawable.ic_baseline_chat_bubble_24,
                R.drawable.ic_baseline_person_outline_24, R.drawable.ic_baseline_folder_open_24)
            // 5. 탭 레이아웃과 뷰 페이저 연결
            TabLayoutMediator(mainTab, veiwPager){ tab, position->
                tab.setIcon(mainFragmentName[position])
            }.attach()

            // 뷰 페이저 스와이프 막기
            veiwPager.isUserInputEnabled = false
            // 탭 레이아웃 선택시 수행 동작 설정
            mainTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    // 뷰 페이저 화면 전환 애니메이션 제거
                    tab?.position?.let{veiwPager.setCurrentItem(it, false)}
                    when(tab!!.position){
                        0->tab.setIcon(R.drawable.ic_baseline_chat_bubble_24)
                        1->tab.setIcon(R.drawable.ic_baseline_person_24)
                        2->tab.setIcon(R.drawable.ic_baseline_folder_24)
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    when(tab!!.position){
                        0->tab.setIcon(R.drawable.ic_baseline_chat_bubble_outline_24)
                        1->tab.setIcon(R.drawable.ic_baseline_person_outline_24)
                        2->tab.setIcon(R.drawable.ic_baseline_folder_open_24)
                    }
                }
            })

        }
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Snackbar.make(binding.veiwPager,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Snackbar.LENGTH_LONG).show()
        } else {
            finish() //액티비티 종료
        }
    }

    fun openChattingActivity(cid:String){
        val intent = Intent(this@MainActivity, ChattingActivity::class.java)
        intent.putExtra("cid",cid)
        startActivity(intent)
    }

    fun openSettingActivity(){
        val intent = Intent(this@MainActivity, SettingActivity::class.java)
        startActivity(intent)
    }
}

class MainFragmentPagerAdapter(val fragmentList: List<Fragment>, fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity){
    override fun getItemCount() = fragmentList.size
    override fun createFragment(position: Int) = fragmentList[position]
}