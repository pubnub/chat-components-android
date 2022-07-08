package com.pubnub.components.data.message

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class CustomContentConverter {
    private val gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .create()

    @TypeConverter
    fun customMapToString(custom: Map<String, Any>?): String? =
        gson.toJson(custom)

    @TypeConverter
    fun customMapFromString(custom: String?): Map<String, Any>? =
        gson.fromJson(custom, object : TypeToken<Map<String, Any>>() {}.type)
}
