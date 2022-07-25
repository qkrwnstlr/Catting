package com.example.catting

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("uid")
    val uid: String?,
    @SerializedName("nickName")
    val nickName: String?,
    @SerializedName("camID")
    val camID: String?,
    @SerializedName("cats")
    var cats:ArrayList<CatProfile>) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(CatProfile.CREATOR) as ArrayList<CatProfile>
    ) {
        Log.d("DataTypeFragment","${this.cats}")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(nickName)
        parcel.writeString(camID)
        parcel.writeTypedList(cats as List<CatProfile>)
    }

    override fun describeContents(): Int {
        return 0
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

data class CatProfile(
    @SerializedName("cid")
    val cid: Int?,
    @SerializedName("cName")
    val cName: String?,
    @SerializedName("cPicture")
    val cPicture: String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(cid!!)
        dest?.writeString(cName)
        dest?.writeString(cPicture)
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

data class CatInfo(
    @SerializedName("cid")
    val cid: Int?,
    @SerializedName("cName")
    val cName: String?,
    @SerializedName("bread")
    val bread: String?,
    @SerializedName("birthday")
    val birthday: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("cPicture")
    val cPicture: String?,
    @SerializedName("bio")
    val bio: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cid!!)
        parcel.writeString(cName)
        parcel.writeString(bread)
        parcel.writeString(birthday)
        parcel.writeString(gender)
        parcel.writeString(cPicture)
        parcel.writeString(bio)
    }

    override fun describeContents(): Int {
        return 0
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


data class  MessageLog(
    @SerializedName("uid")
    val uid: String?,
    @SerializedName("cat_id")
    val cid: Int?,
    @SerializedName("userMessage")
    val userMessage:String?,
    @SerializedName("catAnswer")
    val catAnswer:String?,
    @SerializedName("catImg")
    val image:String?
)