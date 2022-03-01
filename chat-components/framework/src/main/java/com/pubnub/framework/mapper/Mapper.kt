package com.pubnub.framework.mapper

/**
 * Default mapper interface
 */
interface Mapper<in Input, out Output> {
    fun map(input: Input): Output
}

interface MapperWithId<in Input, out Output> {
    fun map(id: String, input: Input): Output
}

