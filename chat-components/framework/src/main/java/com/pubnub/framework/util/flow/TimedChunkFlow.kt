package com.pubnub.framework.util.flow

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

private class TimedChunkFlow<T>(sourceFlow: Flow<T>, duration: Duration) {
    private val chunkLock = ReentrantLock()
    private var chunk = mutableListOf<T>()

    @OptIn(FlowPreview::class)
    val resultFlow = flow {
        sourceFlow.collect {
            // the chunk is reused before it's collected by "sample()"
            val localChunk = chunkLock.withLock {
                chunk.add(it)
                chunk
            }
            emit(localChunk)
        }
    }.sample(duration.inWholeMilliseconds).onEach {
        chunkLock.withLock {
            chunk = mutableListOf()
        }
    }
}

fun <T> Flow<T>.chunked(duration: Duration): Flow<List<T>> = TimedChunkFlow(this, duration).resultFlow