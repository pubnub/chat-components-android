package com.pubnub.components.data.message

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder

class CustomContentConverter {

    @TypeConverter
    fun customAnyToString(custom: Any?): String? =
        if (custom == null) null
        else gson.toJson(custom)

    @TypeConverter
    fun customAnyFromString(custom: String?): Any? =
        gson.fromJson(custom, Any::class.java)
}

internal val gson = GsonBuilder()
    .enableComplexMapKeySerialization()
    .create()

fun <T> Any?.mapTo(type: Class<T>): T? {
    if (this == null || this !is Map<*, *>?) return null

    val json = gson.toJson(this)
    return gson.fromJson(json, type)
}
