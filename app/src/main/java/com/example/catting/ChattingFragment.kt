package com.example.catting

import android.content.Context
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
import com.example.catting.databinding.FragmentChattingBinding
import com.example.catting.databinding.ItemChattingCatListBinding
import io.socket.emitter.Emitter
import org.json.JSONObject

class ChattingFragment : Fragment() {
    lateinit var binding: FragmentChattingBinding
    lateinit var mainActivity: MainActivity
    lateinit var catList: ArrayList<CatProfile>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChattingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            toolbar.inflateMenu(R.menu.main_toolbar)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.setting_icon -> {
                        mainActivity.openSettingActivity()
                        //mainActivity.openSettingActivity()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onResume() {
        Log.d("ChattingFragment","onResume")
        super.onResume()
        // 프래그먼트 재시작
        catList = mainActivity.userInfo.cats
        Log.d("ChattingFragment","$catList")
        binding.chattingRecycler.layoutManager = LinearLayoutManager(mainActivity,LinearLayoutManager.VERTICAL,false)
        binding.chattingRecycler.adapter = CatAdapter(catList)
        /*mainActivity.socket.emit("GetCatInfo","")
        mainActivity.socket.on("GetCatInfo",onGetCatInfo)*/
    }

    override fun onPause() {
        super.onPause()
        // 프래그먼트 전환
        Log.d("ChattingFragment","onPause")
    }
}

class CatAdapter(val listData: ArrayList<CatProfile>) : RecyclerView.Adapter<CatAdapter.Holder>(){
    class Holder(val binding: ItemChattingCatListBinding): RecyclerView.ViewHolder(binding.root){
        fun setCatProfile(catProfile:CatProfile){
            val decodedString = Base64.decode(catProfile.cPicture, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            with(binding){
                cName.text = catProfile.cName
                chattingInfo.setOnClickListener {
                    MainActivity.getInstance()?.openChattingActivity(catProfile)
                }
                cImage.setImageBitmap(decodedByte)
                cImage.clipToOutline = true
                lastChat.text = ""
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemChattingCatListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Log.d("ChattingFragment", "${listData[position]}")
        val catProfile = listData[position]
        holder.setCatProfile(catProfile)
    }

    override fun getItemCount() = listData.size
}