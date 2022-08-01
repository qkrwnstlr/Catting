package com.example.catting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.catting.databinding.DialogProgressBarBinding

class ProgressBarDialog(private val context : AppCompatActivity) {
    private lateinit var binding : DialogProgressBarBinding
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감

    private lateinit var listener : MyDialogOKClickedListener

    fun show() {
        binding = DialogProgressBarBinding.inflate(context.layoutInflater)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(binding.root)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //다이얼로그 배경 투명하게
        dlg.show()
    }

    fun dismiss(){
        dlg.dismiss()
    }

    interface MyDialogOKClickedListener {
        fun onOKClicked(content : String)
    }

}
