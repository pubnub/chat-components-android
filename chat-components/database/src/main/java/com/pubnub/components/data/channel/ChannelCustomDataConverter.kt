package com.pubnub.components.data.channel

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class ChannelCustomDataConverter {

    private val gson = GsonBuilder().create()

    @TypeConverter
    fun stringToCustomData(string: String?): ChannelCustomData? =
        gson.fromJson(string, object : TypeToken<ChannelCustomData?>() {}.type)

    @TypeConverter
    fun customDataToString(custom: ChannelCustomData?): String? = gson.toJson(custom)
}