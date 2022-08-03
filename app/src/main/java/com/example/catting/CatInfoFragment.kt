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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.catting.databinding.FragmentCatInfoBinding
import com.example.catting.databinding.ItemCatInfoCatListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FileFragment : Fragment() {
    lateinit var binding: FragmentCatInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var api:RetrofitApplication

    lateinit var userInfo: UserInfo
    lateinit var catProfileList: ArrayList<CatProfile>
    lateinit var catInfoAdapter: CatInfoAdapter

    lateinit var catInfoResult: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCatInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        api = mainActivity.api
        catInfoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                val data: Intent? = it.data
                val index = data?.getIntExtra("index", -2)
                val result = data?.getLargeExtra<CatProfile>("catProfile")!!
                Log.d("UserInfoFragment","data?.getLargeExtra = ${result.cPicture!!.length}")
                when(index){
                    -2 -> { }
                    -1 -> {
                        userInfo.cats.add(result)
                        mainActivity.userInfo = userInfo
                        MainActivity.isChattingFragmentNeedRefresh = true
                        MainActivity.isCatInfoFragmentNeedRefresh = true
                    }
                    else -> {
                        userInfo.cats[index!!] = result
                        mainActivity.userInfo = userInfo
                        MainActivity.isChattingFragmentNeedRefresh = true
                        MainActivity.isCatInfoFragmentNeedRefresh = true
                    }
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
        }
    }

    override fun onResume() {
        super.onResume()
        if(MainActivity.isCatInfoFragmentNeedRefresh){
            userInfo = mainActivity.userInfo
            catProfileList = arrayListOf<CatProfile>()
            catProfileList.addAll(userInfo.cats)
            catInfoAdapter = CatInfoAdapter(catProfileList, catInfoResult, mainActivity)
            with(binding){
                catInfoRecycler.layoutManager = LinearLayoutManager(mainActivity,
                    LinearLayoutManager.VERTICAL, false)
                catInfoRecycler.adapter = catInfoAdapter
            }
            MainActivity.isCatInfoFragmentNeedRefresh = false
        }
        Log.d("UserInfoFragment","onResume")
    }

    override fun onPause() {
        super.onPause()
        // 프래그먼트 전환
        Log.d("FileFragment","onPause")
    }
}

class CatInfoAdapter(val listData: ArrayList<CatProfile>, val catInfoResult: ActivityResultLauncher<Intent>, val mainActivity: MainActivity) : RecyclerView.Adapter<CatInfoAdapter.Holder>(){
    inner class Holder(val binding: ItemCatInfoCatListBinding): RecyclerView.ViewHolder(binding.root){
        fun setCatProfile(catProfile: CatProfile, position: Int){
            val decodedString = Base64.decode(catProfile.cPicture, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            with(binding){
                cName.text = catProfile.cName
                LinearLayout.setOnClickListener {
                    val intent = Intent(MainActivity.getInstance(), CatInfoActivity::class.java)
                    intent.putExtra("index", position)
                    intent.putLargeExtra("catProfile", catProfile)
                    catInfoResult.launch(intent)
                }
                LinearLayout.setOnLongClickListener{
                    val dlg = DeleteDialog(mainActivity, "Are you sure to delete ${catProfile.cName}'s info?")
                    val api = mainActivity.api
                    dlg.setOnOKClickedListener {
                        when(it){
                            "yes"->{
                                // test
                                removeItem(position)
                                mainActivity.userInfo.cats.removeAt(position)
                                MainActivity.isCatInfoFragmentNeedRefresh = true
                                MainActivity.isChattingFragmentNeedRefresh = true
                                //

                                api.deleteCatInfo(Relation(catProfile.uid, catProfile.cid)).enqueue(object :Callback<Relation>{
                                    override fun onResponse(
                                        call: Call<Relation>,
                                        response: Response<Relation>
                                    ) {
                                        val body = response.body().toString()
                                        if(body.isNotEmpty()){
                                            removeItem(position)
                                            mainActivity.userInfo.cats.removeAt(position)
                                            MainActivity.isCatInfoFragmentNeedRefresh = true
                                            MainActivity.isChattingFragmentNeedRefresh = true
                                        }
                                    }

                                    override fun onFailure(call: Call<Relation>, t: Throwable) {
                                        Log.d("CatInfoFragment",t.message.toString())
                                        Log.d("CatInfoFragment","fail")
                                    }

                                })
                            }
                            "no"->{ }
                        }
                    }
                    dlg.show()
                    return@setOnLongClickListener(true)
                }
                cImage.setImageBitmap(decodedByte)
                cImage.clipToOutline = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemCatInfoCatListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Log.d("ChattingFragment", "${listData[position].cPicture!!.length}")
        val catInfo = listData[position]
        holder.setCatProfile(catInfo, position)
    }

    override fun getItemCount() = listData.size

    fun addItem(catProfile: CatProfile){
        listData.add(catProfile)
        notifyDataSetChanged()
    }

    fun removeItem(index: Int){
        listData.removeAt(index)
        notifyDataSetChanged()
    }

    fun updateItem(index: Int, catProfile: CatProfile){
        listData[index] = catProfile
        notifyDataSetChanged()
    }
}