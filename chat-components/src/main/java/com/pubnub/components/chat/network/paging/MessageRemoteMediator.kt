package com.pubnub.components.chat.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.pubnub.components.chat.network.data.NetworkHistorySyncResult
import com.pubnub.components.chat.network.data.NetworkMessagePayload
import com.pubnub.components.chat.service.message.MessageService
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.data.sync.DBRemoteTimetoken
import com.pubnub.components.repository.sync.RemoteTimetokenRepository
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.service.error.Logger
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalPagingApi::class, DelicateCoroutinesApi::class)
class MessageRemoteMediator constructor(
    private val service: MessageService<NetworkMessagePayload>,
    private val remoteTimetokenRepository: RemoteTimetokenRepository<DBRemoteTimetoken>,
    private val logger: Logger,
    private val messageCount: Int = 10,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RemoteMediator<Int, DBMessageWithActions>() {

    companion object {
        const val TABLE_NAME = "message"
    }

    private lateinit var lastState: PagingState<Int, DBMessageWithActions>

    private var firstMessageTimestamp: Long? = 0L
    private var lastMessageTimestamp: Long? = 0L

    private lateinit var channelId: ChannelId

    fun refresh() {
        coroutineScope.launch(dispatcher) { load(LoadType.REFRESH, lastState) }
    }

    fun setChannel(channelId: ChannelId) {
        this.channelId = channelId
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DBMessageWithActions>,
    ): MediatorResult {

        return withContext(dispatcher) {
            lastState = state
            val page =
                when (loadType) {
                    LoadType.REFRESH -> {
                        logger.d("Refresh")
                        getTimeWindowForInitMessage()
                    }
                    LoadType.PREPEND -> {
                        logger.d("Prepend")
                        getTimeWindowForLastMessage(state)
                    }
                    LoadType.APPEND -> {
                        logger.d("Append")
                        getTimeWindowForFirstMessage(state)
                    }
                }


            logger.d("Page: $page")
            try {
                if (page == null)
                    return@withContext MediatorResult.Success(endOfPaginationReached = false)

                withContext(dispatcher) {
                    loadNewMessages(channelId, loadType, page.start, page.end)
                }

                return@withContext MediatorResult.Success(endOfPaginationReached = false)
            } catch (exception: IOException) {
                return@withContext MediatorResult.Error(exception)
            } catch (exception: Exception) {
                return@withContext MediatorResult.Error(exception)
            }
        }
    }

    private suspend fun getTimeWindowForInitMessage(): MessageWindow {
        // Get the history // refresh
        val start: Timetoken? = System.currentTimeMillis() * 10_000L
        val end: Timetoken? = null

        val window = remoteTimetokenRepository.get(TABLE_NAME, channelId)
        if (window != null) {
            return MessageWindow(channelId, null, window.end)
        }

        return MessageWindow(channelId, start, end)
    }

    // Get the oldest message timetoken
    private suspend fun getTimeWindowForFirstMessage(state: PagingState<Int, DBMessageWithActions>): MessageWindow? {
        // get the lower timetoken
        // last page will contains oldest messages
        val messageTimetoken = state.pages.lastOrNull { it.data.isNotEmpty() }
            ?.data
            ?.minOfOrNull { it.message.timetoken }

        // just skip when nothing changed
        if (messageTimetoken == firstMessageTimestamp) return null
        firstMessageTimestamp = messageTimetoken

        val window = remoteTimetokenRepository.get(TABLE_NAME, channelId) ?: return null

        // there's max one window in current implementation
        return MessageWindow(channelId, window.start, null)
    }

    private suspend fun getTimeWindowForLastMessage(state: PagingState<Int, DBMessageWithActions>): MessageWindow? {

        // first page will contains the newest messages
        val messageTimetoken = state.pages.firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.maxOfOrNull { it.message.timetoken }

        // just skip when nothing changed
        if (messageTimetoken == lastMessageTimestamp) return null
        lastMessageTimestamp = messageTimetoken


        val window = remoteTimetokenRepository.get(TABLE_NAME, channelId)
            ?: return null// getTimeWindowForInitMessage()

        // there's max one window in current implementation
        return MessageWindow(channelId, System.currentTimeMillis() * 10_000L, window.end + 1)
    }

    private fun createRange(
        channelId: ChannelId,
        type: LoadType,
        start: Timetoken?,
        end: Timetoken?,
        min: Timetoken?,
        max: Timetoken?
    ): DBRemoteTimetoken {

        val minTimetoken: Timetoken = when (type) {
            LoadType.REFRESH -> {
                min!!
            }
            LoadType.PREPEND -> {
                min!!
            }
            LoadType.APPEND -> {
                end!!
            }
        }
        val maxTimetoken: Timetoken = when (type) {
            LoadType.REFRESH -> {
                start!!
            }
            LoadType.PREPEND -> {
                start!!
            }
            LoadType.APPEND -> {
                max!!
            }
        }

        return DBRemoteTimetoken(TABLE_NAME, channelId, minTimetoken, maxTimetoken)
    }

    // workaround for sync all the messages on refresh
    private suspend fun syncAll(
        channelId: ChannelId,
        loadType: LoadType,
        start: Long?,
        end: Long?
    ): NetworkHistorySyncResult? {
        logger.d("Sync all $start:$end")

        // Get the history
        var result = service.fetchAll(
            id = channelId,
            start = start,
            end = end,
            count = messageCount,
            withUUID = true,
            withActions = true,
        )

        val fetchMore = loadType == LoadType.REFRESH && // should sync more REFRESH only
                result != null && // last result exists
                (result.page != null || // page exists in response, for default LIMIT parameter or > 25
                        (messageCount <= 25 && result.messageCount == messageCount) // when the LIMIT is <= 25, the page is not defined in response
                        )
        // If there's no more messages, return the result
        if (!fetchMore) return result

        // Check is there page set
        val min = result!!.minTimetoken
        val max = result.maxTimetoken

        result = syncAll(
            channelId = channelId,
            loadType = loadType,
            start = result.page!!.start,
            end = result.page!!.end,
        )
        return NetworkHistorySyncResult(
            nullableMin(min, result?.minTimetoken),
            nullableMax(max, result?.maxTimetoken),
            result?.page,
            result?.messageCount?:0,
        )
    }

    private suspend fun loadNewMessages(
        channelId: ChannelId,
        loadType: LoadType,
        start: Long?,
        end: Long?
    ) {
        logger.d("Load new messages $start:$end")
        // Get the history
        val result = syncAll(
            channelId = channelId,
            loadType = loadType,
            start = start,
            end = end,
        ) ?: return

        logger.d("Received range: ${result.minTimetoken}:${result.maxTimetoken}")
        // Store updated remote timetoken
        val window = remoteTimetokenRepository.get(TABLE_NAME, channelId)
        val newRemoteTimetoken =
            createRange(channelId, loadType, start, end, result.minTimetoken, result.maxTimetoken)

        // there's max one window in current implementation
        if (window == null) {
            logger.d("Insert new range $newRemoteTimetoken")
            remoteTimetokenRepository.insertOrUpdate(newRemoteTimetoken)
        } else {
            val newRange = window + newRemoteTimetoken
            logger.d("Update range $newRange")
            remoteTimetokenRepository.insertOrUpdate(newRange)
        }
    }

    private fun nullableMin(a: Timetoken?, b: Timetoken?) =
        if (a == null && b == null) null
        else if (a == null) b
        else if (b == null) a
        else min(a, b)

    private fun nullableMax(a: Timetoken?, b: Timetoken?) =
        if (a == null && b == null) null
        else if (a == null) b
        else if (b == null) a
        else max(a, b)

}

private data class MessageWindow(
    val channelId: ChannelId,
    val start: Long?,
    val end: Long?,
)
