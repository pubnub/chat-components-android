package com.pubnub.framework.util

import com.pubnub.api.managers.MapperManager
import java.lang.reflect.Type

fun Any?.toJson(mapper: MapperManager) =
    mapper.toJson(this)

inline fun <reified T> String?.fromJson(mapper: MapperManager): T =
    mapper.fromJson(this, T::class.java)

inline fun <reified T> String?.fromJson(mapper: MapperManager, type: Type): T =
    mapper.fromJson(this, type)

inline fun <reified T> Any?.asObject(mapper: MapperManager): T =
    toJson(mapper).fromJson(mapper)
