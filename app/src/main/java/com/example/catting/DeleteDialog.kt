package com.example.catting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.catting.databinding.DialogDeleteBinding

class DeleteDialog(private val context : AppCompatActivity, val title: String) {

    private lateinit var binding : DialogDeleteBinding
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감

    private lateinit var listener : MyDialogOKClickedListener

    fun show() {
        binding = DialogDeleteBinding.inflate(context.layoutInflater)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(binding.root)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //다이얼로그 배경 투명하게

        binding.titleText.text = title

        //ok 버튼 동작
        binding.yes.setOnClickListener {

            listener.onOKClicked("yes")

            dlg.dismiss()
        }

        //cancel 버튼 동작
        binding.no.setOnClickListener {

            listener.onOKClicked("no")

            dlg.dismiss()
        }

        dlg.show()
    }

    fun setOnOKClickedListener(listener: (String) -> Unit) {
        this.listener = object: MyDialogOKClickedListener {
            override fun onOKClicked(content: String) {
                listener(content)
            }
        }
    }


    interface MyDialogOKClickedListener {
        fun onOKClicked(content : String)
    }

}
