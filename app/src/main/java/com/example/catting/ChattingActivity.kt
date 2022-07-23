package com.example.catting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.catting.databinding.*

class ChattingActivity : AppCompatActivity() {
    val binding by lazy { ActivityChattingBinding.inflate(layoutInflater) }
    private lateinit var model: ChattingViewModel
    //private lateinit var bubbleAdapter: BubbleAdapter
    private var page = 1 // 현재 페이지
    var pageSize = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d("NextFragment","onCreate")
    }

    override fun onPause() {
        super.onPause()
        Log.d("NextFragment","onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NextFragment","onDestroy")
    }

}

/*class BubbleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_TEXT_USER = 1
    private val VIEW_TYPE_IMAGE_USER = 2
    private val VIEW_TYPE_TEXT_CAT = 3
    private val VIEW_TYPE_IMAGE_CAT = 4
    private val items = ArrayList<Content>()

    // 아이템뷰에 게시물이 들어가는 경우
    inner class TextUserViewHolder(private val binding: ItemChattingTextUserBubbleBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Content) {
            binding.tvTitle.text = notice.title
            binding.tvDate.text = notice.created
        }
    }

    inner class ImageUserViewHolder(private val binding: ItemChattingImageUserBubbleBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Content) {
            binding.tvTitle.text = notice.title
            binding.tvDate.text = notice.created
        }
    }

    inner class TextCatViewHolder(private val binding: ItemChattingTextCatBubbleBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Content) {
            binding.tvTitle.text = notice.title
            binding.tvDate.text = notice.created
        }
    }

    inner class ImageCatViewHolder(private val binding: ItemChattingImageCatBubbleBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Content) {
            binding.tvTitle.text = notice.title
            binding.tvDate.text = notice.created
        }
    }

    // 아이템뷰에 프로그레스바가 들어가는 경우
    inner class LoadingViewHolder(private val binding: ItemChattingLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    // 뷰의 타입을 정해주는 곳이다.
    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (items[position].title) {
            " " -> VIEW_TYPE_LOADING
            else -> when(items.[position].type)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNoticeBinding.inflate(layoutInflater, parent, false)
                NoticeViewHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is NoticeViewHolder){
            holder.bind(items[position])
        }else{

        }
    }

    fun setList(notice: MutableList<Content>) {
        items.addAll(notice)
        items.add(Content(" ", " ")) // progress bar 넣을 자리
    }

    fun deleteLoading(){
        items.removeAt(items.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
    }
}*/