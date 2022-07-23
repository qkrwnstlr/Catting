package com.example.catting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Content(val title:String, val created:String) {
    val content: MutableList<Content>
        get() {
            val content = mutableListOf<Content>()
            content.add(this)
            return content
        }
}

class ChattingViewModel : ViewModel() {
    // repository 객체 생성
    private val baeminRepository = BaeminRepository()

    // repository에 있는 MutableLiveData를 ViewModel의 LiveData에 넣는다.
    private val baeminNotice: LiveData<Content>
        get() = baeminRepository._baeminNotice

    fun loadBaeminNotice(page:Int){
        baeminRepository.loadBaeminNotice(page) // repository에 있는 메서드를 호출함으로써 다음 공지사항을 불러온다.
    }

    fun getAll(): LiveData<Content> {
        return baeminNotice
    }
}

class BaeminRepository {
    var _baeminNotice = MutableLiveData<Content>() // MutableLiveData 객체 생성

    // ViewModel에서 이 메서드를 호출하면 다음 페이지 공지사항을 불러온다.
    fun loadBaeminNotice(page:Int) {
        /*(웹 사이트에서 공지사항을 불러오는 코드)*/
        _baeminNotice.value = Content("title","$page") // 값의 변경이 일어난다.
    }
}