package com.example.catting

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.catting.databinding.FragmentUserInfoBinding
import com.example.catting.databinding.ItemChattingCatListBinding
import com.example.catting.databinding.ItemUserInfoCatListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class UserInfoFragment : Fragment() {
    lateinit var binding: FragmentUserInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var catInfoList: ArrayList<CatInfo>
    lateinit var catInfoAdapter: CatInfoAdapter
    val api = mainActivity.api
    val userInfo = mainActivity.userInfo

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
            addCatButton.setOnClickListener {
                mainActivity.openCatInfoActivity(null, -1, catInfoAdapter)
            }
            editUserInfoButton.setOnClickListener{
                api.sendCatsInfo(catInfoAdapter.listData).enqueue(object: Callback<UserInfo>{
                    override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                        val body = response.body().toString()
                        if(body.isNotEmpty()){
                            api.sendUserInfo(Gson().fromJson(body,UserInfo::class.java)).enqueue(object: Callback<UserInfo>{
                                override fun onResponse(
                                    call: Call<UserInfo>,
                                    response: Response<UserInfo>
                                ) {
                                    TODO("Not yet implemented")
                                }

                                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                                    TODO("Not yet implemented")
                                }

                            })
                        }
                    }

                    override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                        Log.d("UserInfoFragment",t.message.toString())
                        Log.d("UserInfoFragment","fail")
                    }
                })

            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 프래그먼트 재시작
        api.getCatsInfo(userInfo.uid!!).enqueue(object: Callback<ArrayList<CatInfo>>{
            override fun onResponse(
                call: Call<ArrayList<CatInfo>>,
                response: Response<ArrayList<CatInfo>>
            ) {
                val body = response.body().toString()
                if(body.isNotEmpty()){
                    val listType: TypeToken<ArrayList<CatInfo>> = object: TypeToken<ArrayList<CatInfo>>() {}
                    catInfoList = Gson().fromJson(body,listType.type)
                    catInfoAdapter = CatInfoAdapter(catInfoList)
                    with(binding){
                        catInfoRecycler.layoutManager = LinearLayoutManager(mainActivity,LinearLayoutManager.VERTICAL, false)
                        catInfoRecycler.adapter = catInfoAdapter
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<CatInfo>>, t: Throwable) {
                Log.d("UserInfoFragment",t.message.toString())
                Log.d("UserInfoFragment","fail")
            }

        })
        Log.d("UserInfoFragment","onResume")
    }

    override fun onPause() {
        super.onPause()
        // 프래그먼트 전환
        Log.d("UserInfoFragment","onPause")
    }
}

class CatInfoAdapter(val listData: ArrayList<CatInfo>) : RecyclerView.Adapter<CatInfoAdapter.Holder>(){
    inner class Holder(val binding: ItemUserInfoCatListBinding): RecyclerView.ViewHolder(binding.root){
        fun setCatProfile(catInfo: CatInfo, position: Int){
            val decodedString = Base64.decode(catInfo.cPicture, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            with(binding){
                cName.text = catInfo.cName
                catInformation.setOnClickListener {
                    MainActivity.getInstance()?.openCatInfoActivity(catInfo, position,this@CatInfoAdapter)
                }
                cImage.setImageBitmap(decodedByte)
                cImage.clipToOutline = true
                bread.text = catInfo.bread
                birthday.text = catInfo.birthday
                gender.text = catInfo.gender
                bio.text = catInfo.bio
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemUserInfoCatListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Log.d("ChattingFragment", "${listData[position]}")
        val catInfo = listData[position]
        holder.setCatProfile(catInfo, position)
    }

    override fun getItemCount() = listData.size

    fun addItem(catInfo: CatInfo){
        listData.add(catInfo)
        notifyDataSetChanged()
    }

    fun removeItem(index: Int){
        listData.removeAt(index)
        notifyDataSetChanged()
    }

    fun editItem(index: Int, catInfo: CatInfo){
        listData[index] = catInfo
        notifyDataSetChanged()
    }
}