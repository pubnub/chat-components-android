package com.pubnub.framework.util.data

import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberFunctions

// workaround to use clone-like functionality for classes not marked with 'data' keyword
@Suppress("UNCHECKED_CAST")
fun <T : Any> clone(obj: T, vararg parameters: Pair<String, Any?>): T {
    if (!obj::class.isData) {
        throw NotADataClassException(obj::class.java.name)
    }

    val copy = obj::class.memberFunctions.first { it.name == "copy" }
    val instanceParam = copy.instanceParameter!!
//    val parametersMap = parameters.map { (key, value) -> copy.parameters.firstOrNull { it.name == key } to value }.filter { it.first != null } as List<Pair<KParameter, Any?>>
    val parametersMap =
        parameters.map { (key, value) -> copy.parameters.first { it.name == key } to value }
    return copy.callBy(mapOf(instanceParam to obj, *parametersMap.toTypedArray())) as T
}

class NotADataClassException(clazz: String) :
    Exception("Cannot clone $clazz. Clone is supported for data classes only")