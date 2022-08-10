package com.example.catting

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.catting.databinding.FragmentUserInfoBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserInfoFragment : Fragment() {
    lateinit var binding: FragmentUserInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var api:RetrofitApplication

    lateinit var userInfo: UserInfo

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        api = mainActivity.api
        userInfo = mainActivity.userInfo.copy()

        with(binding){
            toolbar.inflateMenu(R.menu.main_toolbar)
            toolbar.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.setting_icon->{
                        mainActivity.openSettingActivity()
                        true
                    }
                    else -> false
                }
            }

            updateUserInfoButton.setOnClickListener{
                userInfo.nickName = nickName.text.toString()
                userInfo.camID = camID.text.toString()

/*
                // test
                mainActivity.userInfo = userInfo
                MainActivity.isUserInfoFragmentNeedRefresh = true
                mainActivity.binding.mainTab.selectTab(mainActivity.binding.mainTab.getTabAt(0))
                //
*/

                val dlg = ProgressBarDialog(mainActivity)
                if(userInfo.nickName!!.isNotEmpty() && userInfo.camID!!.isNotEmpty()) {
                    dlg.show()
                    api.updateUserInfo(userInfo).enqueue(object : Callback<UserProfile> {
                        override fun onResponse(
                            call: Call<UserProfile>,
                            response: Response<UserProfile>
                        ) {
                            mainActivity.userInfo = UserInfo(
                                response.body()!!,
                                mainActivity.userInfo.cats
                            )
                            MainActivity.isUserInfoFragmentNeedRefresh = true
                            mainActivity.binding.mainTab.selectTab(
                                mainActivity.binding.mainTab.getTabAt(0)
                            )
                        }

                        override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                            Log.d("UserInfoFragment", t.message.toString())
                            Log.d("UserInfoFragment", "fail")
                            dlg.dismiss()
                        }

                    })
                }
                else{
                    Toast.makeText(mainActivity, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            addUserInfoButton.setOnClickListener{
                userInfo.nickName = nickName.text.toString()
                userInfo.camID = camID.text.toString()

/*
                // test
                mainActivity.userInfo = userInfo
                MainActivity.isUserInfoFragmentNeedRefresh = true
                mainActivity.binding.mainTab.selectTab(mainActivity.binding.mainTab.getTabAt(2))
                //
*/

                val dlg = ProgressBarDialog(mainActivity)
                if(userInfo.nickName!!.isNotEmpty() && userInfo.camID!!.isNotEmpty()) {
                    dlg.show()
                    api.addUserInfo(userInfo).enqueue(object : Callback<UserProfile> {
                        override fun onResponse(
                            call: Call<UserProfile>,
                            response: Response<UserProfile>
                        ) {
                            mainActivity.userInfo = UserInfo(response.body()!!)
                            MainActivity.isUserInfoFragmentNeedRefresh = true
                            mainActivity.binding.mainTab.selectTab(
                                mainActivity.binding.mainTab.getTabAt(2)
                            )
                            mainActivity.binding.mainTab.selectTab(mainActivity.binding.mainTab.getTabAt(2))
                        }

                        override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                            Log.d("UserInfoFragment", t.message.toString())
                            Log.d("UserInfoFragment", "fail")
                            dlg.dismiss()
                        }

                    })
                }
                else{
                    Toast.makeText(mainActivity, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 프래그먼트 재시작
        if(mainActivity.userInfo.nickName == null && binding.addUserInfoButton.isInvisible){
            with(binding){
                updateUserInfoButton.visibility = View.INVISIBLE
                addUserInfoButton.visibility = View.VISIBLE
            }
        } else if (binding.addUserInfoButton.isVisible){
            with(binding){
                updateUserInfoButton.visibility = View.VISIBLE
                addUserInfoButton.visibility = View.INVISIBLE
            }
        }

        if(MainActivity.isUserInfoFragmentNeedRefresh){
            with(binding){
                userInfo = mainActivity.userInfo.copy()
                nickName.setText(userInfo.nickName)
                camID.setText(userInfo.camID)
            }
            MainActivity.isUserInfoFragmentNeedRefresh = false
        }
        Log.d("UserInfoFragment","onResume")
    }

    override fun onPause() {
        super.onPause()
        // 프래그먼트 전환
        Log.d("UserInfoFragment","onPause")
    }
}