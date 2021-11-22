package com.pubnub.components.chat.util.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}