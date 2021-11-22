package com.pubnub.components.data.message

import androidx.room.TypeConverter
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class MessageAttachmentConverter {
    val gson = GsonBuilder()
        .registerTypeAdapter(DBAttachment::class.java, attachmentDeserializer())
        .registerTypeAdapter(DBAttachment::class.java, attachmentSerializer())
        .enableComplexMapKeySerialization()
        .create()

    fun attachmentDeserializer(): JsonDeserializer<DBAttachment> =
        JsonDeserializer { jsonElement: JsonElement, _: Type, _: JsonDeserializationContext ->
            val jsonObject = jsonElement.asJsonObject

            when (val objectType = jsonObject.get("type").asString) {
                "link" -> gson.fromJson(jsonElement.toString(), DBAttachment.Link::class.java)
                "image" -> gson.fromJson(jsonElement.toString(), DBAttachment.Image::class.java)
                "custom" -> gson.fromJson(jsonElement.toString(), DBAttachment.Custom::class.java)
                else -> throw RuntimeException("Unknown type '$objectType'")
            }
        }

    fun attachmentSerializer(): JsonSerializer<DBAttachment> =
        JsonSerializer { src, _, context ->
            when (src) {
                is DBAttachment.Image -> {
                    context.serialize(src, DBAttachment.Image::class.java).asJsonObject
                }
                is DBAttachment.Link -> {
                    context.serialize(src, DBAttachment.Link::class.java).asJsonObject
                }
                else -> throw RuntimeException()
            }
        }

    @TypeConverter
    fun attachmentToString(custom: DBAttachment): String? =
        when (custom) {
            is DBAttachment.Image -> imageToString(custom)
            is DBAttachment.Link -> linkToString(custom)
            else -> null
        }

    @TypeConverter
    fun attachmentFromString(custom: String?): DBAttachment? =
        gson.fromJson(custom, DBAttachment::class.java)


    @TypeConverter
    fun attachmentListToString(custom: List<DBAttachment>?): String? {
        println("Custom $custom")

        return gson.toJson(custom)

    }

    @TypeConverter
    fun attachmentListFromString(custom: String?): List<DBAttachment>? =
        gson.fromJson(custom, object : TypeToken<List<DBAttachment>>() {}.type)


    @TypeConverter
    fun customMapToString(custom: Map<String, Any>?): String? =
        gson.toJson(custom)

    @TypeConverter
    fun customMapFromString(custom: String?): Map<String, Any>? =
        gson.fromJson(custom, object : TypeToken<Map<String, Any>>() {}.type)


    fun imageToString(custom: DBAttachment.Image): String? = gson.toJson(custom)

    fun linkToString(custom: DBAttachment.Link): String? = gson.toJson(custom)
}