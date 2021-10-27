package com.pubnub.components.data.member

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder

class MemberDataCustomConverter {

    private val gson = GsonBuilder().create()

    @TypeConverter
    fun stringToCustomData(string: String?): DBMember.CustomData? =
        gson.fromJson(string, DBMember.CustomData::class.java)

    @TypeConverter
    fun customDataToString(custom: DBMember.CustomData?): String? = gson.toJson(custom)
}