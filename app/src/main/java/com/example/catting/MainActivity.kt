package com.example.catting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.catting.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
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

    fun openChattingActivity(){
        val intent = Intent(this@MainActivity, ChattingActivity::class.java)
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