package com.example.catting

import android.os.Parcel
import android.os.Parcelable

class UserInfo(val uid: String?, val nickName: String?, val camID: String?, val cats:ArrayList<CatProfile>) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("cats")
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

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class CatProfile(val cid: String, val cName: String, val cPicture: String)
data class CatInfo(val cid: String, val cName: String, val bread: String, val birthday: String, val gender: String, val cPicture: String, val bio: String)
