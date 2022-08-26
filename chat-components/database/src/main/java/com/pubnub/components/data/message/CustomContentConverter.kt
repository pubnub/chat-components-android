package com.pubnub.components.data.message

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import com.pubnub.components.data.channel.CustomDataMap

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
    .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
    .create()

fun <T> Any?.mapTo(type: Class<T>): T? {
    if (this == null || (this !is Map<*, *>? && this !is JsonElement)) return null

    val json = gson.toJson(this)
    return gson.fromJson(json, type)
}

fun <T> Any?.mapTo(typeToken: TypeToken<T>): T? {
    if (this == null || (this !is Map<*, *>? && this !is JsonElement)) return null

    val json = gson.toJson(this)
    return gson.fromJson(json, typeToken.type)
}

fun Any?.asMap(): CustomDataMap? = this.mapTo(object : TypeToken<CustomDataMap>() {})
