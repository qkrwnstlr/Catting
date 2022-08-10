package com.example.catting

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Uid(
    @SerializedName("uid")
    val uid: String?
)

data class UserProfile(
    @SerializedName("uid")
    val uid: String?,
    @SerializedName("nickName")
    var nickName: String?,
    @SerializedName("camID")
    var camID: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(nickName)
        parcel.writeString(camID)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun copy(): UserProfile{
        return Gson().fromJson(Gson().toJson(this), this::class.java)
    }

    companion object CREATOR : Parcelable.Creator<UserProfile> {
        override fun createFromParcel(parcel: Parcel): UserProfile {
            return UserProfile(parcel)
        }

        override fun newArray(size: Int): Array<UserProfile?> {
            return arrayOfNulls(size)
        }
    }
}

data class  UserInfo(
    @SerializedName(value = "uid", alternate = ["id"])
    val uid: String?,
    @SerializedName("nickName")
    var nickName: String?,
    @SerializedName("camID")
    var camID: String?,
    @SerializedName("cats")
    var cats:ArrayList<CatProfile>) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(CatProfile.CREATOR) as ArrayList<CatProfile>
    )
    constructor(userProfile: UserProfile, cats: ArrayList<CatProfile> = arrayListOf<CatProfile>())
            : this(userProfile.uid, userProfile.nickName, userProfile.camID, cats)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(nickName)
        parcel.writeString(camID)
        parcel.writeTypedList(cats as List<CatProfile>)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun copy(): UserInfo{
        return Gson().fromJson(Gson().toJson(this), this::class.java)
    }

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class CatInfo(
    @SerializedName("uid")
    var uid: String?,
    @SerializedName("cid")
    var cid: Int?,
    @SerializedName("cName")
    var cName: String?,
    @SerializedName("breed")
    var breed: String?,
    @SerializedName("birthday")
    var birthday: String?,
    @SerializedName("gender")
    var gender: String?,
    @SerializedName("cPicture")
    var cPicture: String?,
    @SerializedName("bio")
    var bio: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeValue(cid)
        parcel.writeString(cName)
        parcel.writeString(breed)
        parcel.writeString(birthday)
        parcel.writeString(gender)
        parcel.writeString(cPicture)
        parcel.writeString(bio)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun copy(): CatInfo{
        return Gson().fromJson(Gson().toJson(this), this::class.java)
    }

    companion object CREATOR : Parcelable.Creator<CatInfo> {
        override fun createFromParcel(parcel: Parcel): CatInfo {
            return CatInfo(parcel)
        }

        override fun newArray(size: Int): Array<CatInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class CatProfile(
    @SerializedName("uid")
    var uid: String?,
    @SerializedName("cid")
    var cid: Int?,
    @SerializedName("cName")
    var cName: String?,
    @SerializedName("cPicture")
    var cPicture: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeValue(cid)
        parcel.writeString(cName)
        parcel.writeString(cPicture)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun copy(): CatProfile{
        return Gson().fromJson(Gson().toJson(this), this::class.java)
    }

    companion object CREATOR : Parcelable.Creator<CatProfile> {
        override fun createFromParcel(parcel: Parcel): CatProfile {
            return CatProfile(parcel)
        }

        override fun newArray(size: Int): Array<CatProfile?> {
            return arrayOfNulls(size)
        }
    }
}

data class Relation(
    @SerializedName("uid")
    var uid: String?,
    @SerializedName("cid")
    var cid: Int?
)

@Entity(tableName = "chatting_log")
class ChattingLog{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var no:Long? = null
    @SerializedName("uid")
    @ColumnInfo
    var uid:String? = null
    @SerializedName("cat_id")
    @ColumnInfo
    var cid:Int? = 0
    @SerializedName("userMessage")
    @ColumnInfo
    var userMessage: String? = ""
    @SerializedName("catAnswer")
    @ColumnInfo
    var catAnswer:String? = ""
    @SerializedName("catImg")
    @ColumnInfo
    var image:String? = ""

    constructor(uid: String?, cid: Int?, userMessage: String?, catAnswer:String?, image:String?){
        this.uid = uid
        this.cid = cid
        this.userMessage = userMessage
        this.catAnswer = catAnswer
        this.image = image
    }
}

@Dao
interface ChattingLogDAO{
    @Query("select * from chatting_log where uid = :uid AND cid = :cid")
    fun getAll(uid: String?, cid: Int?):List<ChattingLog>

    @Query("select * from chatting_log where uid = :uid AND cid = :cid AND `no` between :end AND :start")
    fun getPart(uid: String?, cid: Int?, start: Int?, end: Int?):List<ChattingLog>

    @Query("select MAX(`no`) from chatting_log")
    fun getMax():Int

    @Insert(onConflict = REPLACE)
    fun insert(log:ChattingLog)

    @Delete
    fun delete(log:ChattingLog)
}

@Database(entities = arrayOf(ChattingLog::class), version = 1, exportSchema = false)
abstract class ChattingLogHelper: RoomDatabase(){
    abstract fun chattingLogDao(): ChattingLogDAO
}

@Entity(tableName = "sign_in_info")
class SignInInfo(){
    @PrimaryKey(autoGenerate = false)
    var no:Int? = 1
    @ColumnInfo
    var email:String? = ""
    @ColumnInfo
    var password:String? = ""
    constructor(email:String?, password:String?) : this() {
        this.no = 1
        this.email = email
        this.password = password
    }
}

@Dao
interface SignInInfoDao{
    @Query("select * from sign_in_info where `no` = 1")
    fun getSignInInfo(): List<SignInInfo>

    @Insert(onConflict = REPLACE)
    fun updateSignInInfo(signInInfo: SignInInfo)
}

@Database(entities = arrayOf(SignInInfo::class), version = 1, exportSchema = false)
abstract class SignInInfoHelper: RoomDatabase(){
    abstract fun signInInfoDao(): SignInInfoDao
}