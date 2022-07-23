package com.example.catting

import android.content.Context
import android.os.Bundle
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
//                        mainActivity.openSettingActivity()
                        mainActivity.openChattingActivity()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 프래그먼트 재시작
        catList = mainActivity.userInfo.cats
        binding.chattingRecycler.layoutManager = LinearLayoutManager(mainActivity,LinearLayoutManager.VERTICAL,false)
        binding.chattingRecycler.adapter = CatAdapter(catList)
        /*mainActivity.socket.emit("GetCatInfo","")
        mainActivity.socket.on("GetCatInfo",onGetCatInfo)*/
        Log.d("ChattingFragment","onResume")
    }

    override fun onPause() {
        super.onPause()
        // 프래그먼트 전환
        Log.d("ChattingFragment","onPause")
    }

    /*var onGetCatInfo = Emitter.Listener { args ->
        val obj = JSONObject(args[0].toString())
        var text: String
        Thread(object : Runnable{
            override fun run() {
                mainActivity.runOnUiThread(Runnable {
                    kotlin.run {
                        text = "" + obj.get("cName") + ": " + obj.get("message")
                    }
                })
            }
        }).start()
    }*/
}

class CatAdapter(val listData: ArrayList<CatProfile>) : RecyclerView.Adapter<CatAdapter.Holder>(){
    class Holder(val binding: ItemChattingCatListBinding): RecyclerView.ViewHolder(binding.root){
        fun setCatProfile(catProfile:CatProfile){
            with(binding){
                cName.text = catProfile.cName
                // 다른거도 세팅해주기
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemChattingCatListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val catProfile = listData[position]
        holder.setCatProfile(catProfile)
    }

    override fun getItemCount() = listData.size
}