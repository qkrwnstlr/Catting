package com.example.catting

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.catting.databinding.ActivityCatInfoBinding
import java.io.ByteArrayOutputStream

class CatInfoActivity : BaseActivity() {
    lateinit var cameraResult: ActivityResultLauncher<Intent>
    lateinit var galleryResult: ActivityResultLauncher<Intent>
    lateinit var catInfo: CatInfo
    lateinit var cPicture: Bitmap
    var index = -2

    val PERM_STORAGE = 9
    val PERM_CAMEARA = 10

    val binding by lazy { ActivityCatInfoBinding.inflate(layoutInflater) }

    private val actionBar get() = supportActionBar!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        //Toolbar에 표시되는 제목의 표시 유무를 설정. false로 해야 custom한 툴바의 이름이 화면에 보인다.
        actionBar.setDisplayShowTitleEnabled(false)
        //왼쪽 버튼 사용설정(기본은 뒤로가기)
        actionBar.setDisplayHomeAsUpEnabled(true)

        index = intent.getIntExtra("index", -2)
        if(intent.hasExtra("catInfo")){
            catInfo = intent.getParcelableExtra<CatInfo>("catInfo")!!
            with(binding){
                cName.setText(catInfo.cName)
                bread.setText(catInfo.bread)
                birthday.setText(catInfo.birthday)
                gender.setText(catInfo.gender)
                bio.setText(catInfo.bio)
                val decodeString = Base64.decode(catInfo.cPicture, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
                cPicture = decodedByte
                imagePreview.setImageBitmap(decodedByte)
                addCatButton.text = "Edit Cat Info"
            }
        } else {
            var cid = 0
            for(i in 0..10){
                cid = System.currentTimeMillis().toInt()
                Log.d("CatInfoActivity","$cid")
            }
            catInfo = CatInfo(cid,null,null,null,null,null,null)
        }


        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val data: Intent? = it.data
                cPicture = data?.extras?.get("data") as Bitmap
                binding.imagePreview.setImageBitmap(cPicture)
            }
        }

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val data: Intent? = it.data
                var uri : Uri? = data?.data
                if(Build.VERSION.SDK_INT < 28) {
                    cPicture = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        uri
                    )
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, uri!!)
                    cPicture = ImageDecoder.decodeBitmap(source)
                }
                binding.imagePreview.setImageBitmap(cPicture)
                Log.d("CatInfoActivity","$cPicture")
            }
        }


        // 1. 공용저장소 권한이 있는지 확인
        requirePermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERM_STORAGE)
    }

    fun setViews(){
        Log.d("CatInfoActivity","setViews()")
        with(binding){
            imagePreview.setOnClickListener{
                val dlg = CatInfoDialog(this@CatInfoActivity)
                dlg.setOnOKClickedListener {
                    when(it){
                        "gallery"->{
                            openGallery()
                        }
                        "camera"->{
                            // 2. 카메라 요청시 권한을 먼저 체크하고 승인되면 카메라를 연다.
                            requirePermissions(arrayOf(Manifest.permission.CAMERA), PERM_CAMEARA)
                        }
                    }
                }
                dlg.show()
            }
            addCatButton.setOnClickListener {
                Log.d("CatInfoActivity","addCatButton")
                if(cName.text.toString().isEmpty() || bread.text.toString().isEmpty() || birthday.text.toString().isEmpty() || gender.text.toString().isEmpty() || bio.text.toString().isEmpty() || !::cPicture.isInitialized){
                    Toast.makeText(this@CatInfoActivity, "값을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    val intent = Intent(this@CatInfoActivity, MainActivity::class.java)
                    catInfo.cName = cName.text.toString()
                    catInfo.bread = bread.text.toString()
                    catInfo.birthday = birthday.text.toString()
                    catInfo.gender = gender.text.toString()
                    catInfo.bio = bio.text.toString()
                    val stream = ByteArrayOutputStream()
                    cPicture.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    catInfo.cPicture = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
                    intent.putExtra("catInfo", catInfo)
                    intent.putExtra("index", index)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(intent)
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryResult.launch(intent)
    }

    override fun permissionGranted(requestCode: Int) {
        when(requestCode){
            PERM_STORAGE -> setViews()
            PERM_CAMEARA -> openCamera()
        }
    }

    override fun permissionDenied(requestCode: Int) {
        when(requestCode){
            PERM_STORAGE -> {
                Toast.makeText(this,"저장소 권한 승인 필요", Toast.LENGTH_SHORT).show()
                finish()
            }
            PERM_CAMEARA -> {
                Toast.makeText(this,"카메라 권한 승인 필요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //item 버튼 메뉴 Toolbar에 집어 넣기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting_toolbar, menu)
        return true
    }

    //item 버튼 클릭 했을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //뒤로가기 버튼 눌렀을 때
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}