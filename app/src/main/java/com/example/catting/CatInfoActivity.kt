package com.example.catting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.catting.databinding.ActivityCatInfoBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class CatInfoActivity : BaseActivity() {
    lateinit var api: RetrofitApplication

    lateinit var cameraResult: ActivityResultLauncher<Intent>
    lateinit var galleryResult: ActivityResultLauncher<Intent>
    lateinit var catProfile: CatProfile
    lateinit var catInfo:CatInfo
    lateinit var cPicture: Bitmap

    var index = -2

    val PERM_STORAGE = 9
    val PERM_CAMEARA = 10

    val binding by lazy { ActivityCatInfoBinding.inflate(layoutInflater) }

    private val actionBar get() = supportActionBar!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        api = MainActivity.getInstance()?.api!!

        setSupportActionBar(binding.toolbar)
        //Toolbar에 표시되는 제목의 표시 유무를 설정. false로 해야 custom한 툴바의 이름이 화면에 보인다.
        actionBar.setDisplayShowTitleEnabled(false)
        //왼쪽 버튼 사용설정(기본은 뒤로가기)
        actionBar.setDisplayHomeAsUpEnabled(true)

        index = intent.getIntExtra("index", -2)
        if(intent.hasExtra("catProfile")){
            catProfile = intent.getLargeExtra<CatProfile>("catProfile")!!

            //<<>>
            catInfo = CatInfo(null,null,null,null,null,null,null,null)
            //

            api.getCatInfo(Relation(catProfile.uid, catProfile.cid)).enqueue(object: Callback<CatInfo>{
                override fun onResponse(call: Call<CatInfo>, response: Response<CatInfo>) {
                    val catInfo = response.body()!!
                    Thread{
                        runOnUiThread(Runnable {
                            kotlin.run {
                                with(binding){
                                    cName.setText(catInfo.cName)
                                    breed.setText(catInfo.breed)
                                    birthday.setText(catInfo.birthday)
                                    gender.setText(catInfo.gender)
                                    bio.setText(catInfo.bio)
                                    val decodeString = Base64.decode(
                                        catInfo.cPicture,
                                        Base64.DEFAULT
                                    )
                                    val decodedByte = BitmapFactory.decodeByteArray(
                                        decodeString,
                                        0,
                                        decodeString.size
                                    )
                                    cPicture = decodedByte
                                    imagePreview.setImageBitmap(decodedByte)
                                    addCatButton.visibility = View.INVISIBLE
                                    updateCatButton.visibility = View.VISIBLE
                                }
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<CatInfo>, t: Throwable) {
                    Log.d("ChattingActivity",t.message.toString())
                    Log.d("ChattingActivity","fail")
                }

            })
        } else {
            val cid = System.currentTimeMillis().toInt()
            catInfo = CatInfo(MainActivity.getInstance()?.userInfo!!.uid,cid,null,null,null,null,null,null)
            with(binding){
                addCatButton.visibility = View.VISIBLE
                updateCatButton.visibility = View.INVISIBLE
            }
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
                cPicture = if(Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        uri
                    )
                } else {
                    getResizeBitmap(this@CatInfoActivity, uri!!, 160)
                }
                binding.imagePreview.setImageBitmap(cPicture)
            }
        }

        // 1. 공용저장소 권한이 있는지 확인
        requirePermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERM_STORAGE)
    }

    fun getResizeBitmap(context: Context,uri: Uri,resize: Int): Bitmap{
        val resizeBitmap:Bitmap
        val options = BitmapFactory.Options()
        BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)
        var width = options.outWidth
        var height = options.outHeight
        var sampleSize = 1
        while (true) {//2번
            if (width / 2 < resize || height / 2 < resize)
                break;
            width /= 2;
            height /= 2;
            sampleSize *= 2;
        }
        options.inSampleSize = sampleSize;
        resizeBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)!!
        return resizeBitmap
    }

    fun setViews(){
        with(binding){
            imagePreview.setOnClickListener{
                val dlg = CatInfoPictureDialog(this@CatInfoActivity)
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
                if(cName.text.toString().isEmpty() || breed.text.toString().isEmpty() || birthday.text.toString().isEmpty() || gender.text.toString().isEmpty() || bio.text.toString().isEmpty() || !::cPicture.isInitialized){
                    Toast.makeText(this@CatInfoActivity, "값을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    val dlg = ProgressBarDialog(this@CatInfoActivity)
                    dlg.show()
                    catInfo.uid = MainActivity.getInstance()?.userInfo!!.uid
                    catInfo.cName = cName.text.toString()
                    catInfo.breed = breed.text.toString()
                    catInfo.birthday = birthday.text.toString()
                    catInfo.gender = gender.text.toString()
                    catInfo.bio = bio.text.toString()
                    val stream = ByteArrayOutputStream()
                    cPicture.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    catInfo.cPicture = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
/*

                //test
                catProfile = CatProfile(catInfo.uid, catInfo.cid, catInfo.cName, catInfo.cPicture)
                val intent = Intent(
                    this@CatInfoActivity,
                    MainActivity::class.java).apply {
                    putExtra("index", index)
                    putLargeExtra("catProfile", catProfile)
                }
                setResult(RESULT_OK, intent)
                dlg.dismiss()
                finish()
                //

*/
                    api.addCatInfo(catInfo).enqueue(object: Callback<CatProfile>{
                        override fun onResponse(
                            call: Call<CatProfile>,
                            response: Response<CatProfile>
                        ) {
                            catProfile = response.body()!!
                            val intent = Intent(
                                this@CatInfoActivity,
                                MainActivity::class.java).apply {
                                putExtra("index", index)
                                putLargeExtra("catProfile", catProfile)
                            }
                            setResult(RESULT_OK, intent)
                            dlg.dismiss()
                            finish()
                        }

                        override fun onFailure(call: Call<CatProfile>, t: Throwable) {
                            Log.d("CatInfoActivity",t.message.toString())
                            Log.d("CatInfoActivity","fail")
                        }

                    })
                }
            }
            updateCatButton.setOnClickListener {
                Log.d("CatInfoActivity","addCatButton")
                if(cName.text.toString().isEmpty() || breed.text.toString().isEmpty() || birthday.text.toString().isEmpty() || gender.text.toString().isEmpty() || bio.text.toString().isEmpty() || !::cPicture.isInitialized){
                    Toast.makeText(this@CatInfoActivity, "값을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    val dlg = ProgressBarDialog(this@CatInfoActivity)
                    dlg.show()
                    catInfo.cName = cName.text.toString()
                    catInfo.breed = breed.text.toString()
                    catInfo.birthday = birthday.text.toString()
                    catInfo.gender = gender.text.toString()
                    catInfo.bio = bio.text.toString()
                    val stream = ByteArrayOutputStream()
                    cPicture.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    catInfo.cPicture = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
                    api.updateCatInfo(catInfo).enqueue(object: Callback<CatProfile>{
                        override fun onResponse(
                            call: Call<CatProfile>,
                            response: Response<CatProfile>
                        ) {
                            catProfile = response.body()!!
                            val intent = Intent(
                                this@CatInfoActivity,
                                MainActivity::class.java).apply {
                                putExtra("index", index)
                                putLargeExtra("catInfo", catInfo)
                            }
                            setResult(RESULT_OK, intent)
                            dlg.dismiss()
                            finish()
                        }

                        override fun onFailure(call: Call<CatProfile>, t: Throwable) {
                            Log.d("CatInfoActivity",t.message.toString())
                            Log.d("CatInfoActivity","fail")
                        }

                    })
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