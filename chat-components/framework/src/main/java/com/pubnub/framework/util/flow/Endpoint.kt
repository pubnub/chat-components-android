package com.pubnub.framework.util.flow

import com.pubnub.api.Endpoint
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.framework.util.data.PNException
import com.pubnub.framework.util.data.PNResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// region Flow
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <Input, Output> Endpoint<Input, Output>.single(
    onComplete: (Output) -> Unit,
    onError: (Exception) -> Unit = {},
    onStatus: (PNStatus) -> Unit = {}
) = try {
    val result = singleResult()
    onComplete.invoke(result.result!!)
    onStatus.invoke(result.status)
} catch (e: Exception) {
    if (e is CancellationException && e.cause is PNException) {
        val exception = e.cause as PNException
        onError.invoke(exception)
        onStatus.invoke(exception.status)
    } else {
        onError.invoke(e)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <Input, Output> Endpoint<Input, Output>.singleResult(
    onComplete: (Output, PNStatus) -> Unit,
    onError: (Exception) -> Unit = {}
) = try {
    val result = singleResult()
    onComplete.invoke(result.result!!, result.status)
} catch (e: Exception) {
    if (e is CancellationException && e.cause is PNException) {
        val exception = e.cause as PNException
        onError.invoke(exception)
    } else {
        onError.invoke(e)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <Input, Output> Endpoint<Input, Output>.single(): Output =
    this.flow().first()

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <Input, Output> Endpoint<Input, Output>.singleResult(): PNResult<Output> =
    this.flowResult().first()

@OptIn(ExperimentalCoroutinesApi::class)
private fun <Input, Output> Endpoint<Input, Output>.flow(): Flow<Output> =
    callbackFlow {
        val callback: (Output?, PNStatus) -> Unit = { result: Output?, status: PNStatus ->
            if (status.error) cancel(status.exception!!.errorMessage!!, status.exception!!)
            else trySendBlocking(result!!)
            //silentCancel()
        }
        async(callback)

        awaitClose { this@flow.silentCancel() }
    }

@OptIn(ExperimentalCoroutinesApi::class)
private fun <Input, Output> Endpoint<Input, Output>.flowResult(): Flow<PNResult<Output>> =
    callbackFlow {
        val callback: (Output?, PNStatus) -> Unit = { result, status ->
            if (status.error) cancel(
                status.exception!!.errorMessage!!,
                PNException(status.exception!!, status)
            )
            else trySendBlocking(PNResult(result, status))
            //silentCancel()
        }
        async(callback)

        awaitClose { this@flowResult.silentCancel() }
    }
// endregion

// region Coroutine
suspend fun <Input, Output> Endpoint<Input, Output>.coroutine(): Output =
    suspendCoroutine { continuation ->
        val callback = { result: Output?, status: PNStatus ->
            if (status.error) continuation.resumeWithException(
                PNException(
                    status.exception!!,
                    status
                )
            )
            else continuation.resume(result!!)
        }
        async(callback)
    }

suspend fun <Input, Output> Endpoint<Input, Output>.coroutineResult(): PNResult<Output> =
    suspendCoroutine { continuation ->
        val callback = { result: Output?, status: PNStatus ->
            continuation.resume(PNResult(result, status))
        }
        async(callback)
    }
// endregion