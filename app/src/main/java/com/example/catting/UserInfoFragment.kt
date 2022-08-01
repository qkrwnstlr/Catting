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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.catting.databinding.FragmentUserInfoBinding
import com.example.catting.databinding.ItemUserInfoCatListBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserInfoFragment : Fragment() {
    lateinit var binding: FragmentUserInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var catInfoList: ArrayList<CatInfo>
    lateinit var catInfoAdapter: CatInfoAdapter
    lateinit var api:RetrofitApplication
    lateinit var userInfo: UserInfo
    lateinit var catInfoResult: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
        if(context is SignInActivity) mainActivity = MainActivity.getInstance()!!
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

        catInfoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                val data: Intent? = it.data
                val index = data?.getIntExtra("index", -2)
                val result = data?.getLargeExtra<CatInfo>("catInfo")!!
                Log.d("UserInfoFragment","data?.getLargeExtra = ${result.cPicture!!.length}")
                when(index){
                    -2 -> { }
                    -1 -> catInfoAdapter.addItem(result)
                    else -> catInfoAdapter.editItem(index!!, result)
                }
            }
        }

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
                val intent = Intent(mainActivity, CatInfoActivity::class.java)
                intent.putExtra("index", -1)
                catInfoResult.launch(intent)
            }
            editUserInfoButton.setOnClickListener{
                userInfo.nickName = nickName.text.toString()
                userInfo.camID = camID.text.toString()
                userInfo.cats = catInfoAdapter.listData

                // test
                mainActivity.userInfo = userInfo
                MainActivity.isUserInfoFragmentNeedRefresh = true
                MainActivity.isChattingFragmentNeedRefresh = true
                mainActivity.binding.mainTab.selectTab(mainActivity.binding.mainTab.getTabAt(0))
                //
                val dlg = ProgressBarDialog(mainActivity)
                if(userInfo.nickName!!.isNotEmpty() && userInfo.camID!!.isNotEmpty()) {
                    dlg.show()
                    api.sendUserInfo(userInfo).enqueue(object : Callback<UserInfo> {
                        override fun onResponse(
                            call: Call<UserInfo>,
                            response: Response<UserInfo>
                        ) {
                            val body = response.body().toString()
                            if (body.isNotEmpty()) {
                                mainActivity.userInfo = Gson().fromJson(body, UserInfo::class.java)
                                MainActivity.isUserInfoFragmentNeedRefresh = true
                                MainActivity.isChattingFragmentNeedRefresh = true
                                mainActivity.binding.mainTab.selectTab(
                                    mainActivity.binding.mainTab.getTabAt(
                                        0
                                    )
                                )
                            }
                        }

                        override fun onFailure(call: Call<UserInfo>, t: Throwable) {
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
        if(MainActivity.isUserInfoFragmentNeedRefresh){
            catInfoList = userInfo.cats
            catInfoAdapter = CatInfoAdapter(catInfoList, catInfoResult)
            with(binding){
                nickName.setText(userInfo.nickName)
                camID.setText(userInfo.camID)
                catInfoRecycler.layoutManager = LinearLayoutManager(mainActivity,LinearLayoutManager.VERTICAL, false)
                catInfoRecycler.adapter = catInfoAdapter

                val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
                itemTouchHelper.attachToRecyclerView(catInfoRecycler)
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

    var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                catInfoAdapter.removeItem(position)
            }
        }
}

class CatInfoAdapter(val listData: ArrayList<CatInfo>, val catInfoResult: ActivityResultLauncher<Intent>) : RecyclerView.Adapter<CatInfoAdapter.Holder>(){
    inner class Holder(val binding: ItemUserInfoCatListBinding): RecyclerView.ViewHolder(binding.root){
        fun setCatProfile(catInfo: CatInfo, position: Int){
            val decodedString = Base64.decode(catInfo.cPicture, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            with(binding){
                cName.text = catInfo.cName
                catInformation.setOnClickListener {
                    val intent = Intent(MainActivity.getInstance(),CatInfoActivity::class.java)
                    intent.putExtra("index", position)
                    intent.putLargeExtra("catInfo", catInfo)
                    catInfoResult.launch(intent)
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
        Log.d("ChattingFragment", "${listData[position].cPicture!!.length}")
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