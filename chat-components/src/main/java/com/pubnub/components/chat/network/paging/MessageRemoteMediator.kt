package com.pubnub.components.chat.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.pubnub.components.chat.service.message.MessageService
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.repository.message.MessageRepository
import com.pubnub.framework.data.ChannelId
import kotlinx.coroutines.*
import java.io.IOException

@OptIn(ExperimentalPagingApi::class, DelicateCoroutinesApi::class)
class MessageRemoteMediator constructor(
    private val channelId: ChannelId,
    private val service: MessageService<DBMessage>,
    private val messageRepository: MessageRepository<DBMessage, DBMessage>,
    private val messageCount: Int = 10,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RemoteMediator<Int, DBMessage>() {

    private lateinit var lastState: PagingState<Int, DBMessage>

    private var firstMessageTimestamp: Long? = 0L
    private var lastMessageTimestamp: Long? = 0L

    fun refresh() {
        coroutineScope.launch(dispatcher) { load(LoadType.REFRESH, lastState) }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DBMessage>,
    ): MediatorResult {
        lastState = state
        val page = when (loadType) {
            LoadType.REFRESH -> {
                getTimeWindowForInitMessage()
            }
            LoadType.PREPEND -> {
                getTimeWindowForLastMessage(state)
            }
            LoadType.APPEND -> {
                getTimeWindowForFirstMessage(state)
            }
        }

        try {
            if (page == null)
                return MediatorResult.Success(endOfPaginationReached = false)

            withContext(dispatcher) {
                loadNewMessages(channelId, page.start, page.end)
            }

            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getTimeWindowForInitMessage(): MessageWindow? {

        if (messageRepository.getLast(channelId) != null)
            return null

        // Get the history // refresh
        val start = System.currentTimeMillis() * 10_000L
        val end = null

        return MessageWindow(channelId, start, end)
    }

    private suspend fun getTimeWindowForFirstMessage(state: PagingState<Int, DBMessage>): MessageWindow? {
        // last page will contains oldest messages
        val messageTimetoken = state.pages.lastOrNull { it.data.isNotEmpty() }
            ?.data
            ?.minOf { it.timetoken }

        // just skip when nothing changed
        if (messageTimetoken == firstMessageTimestamp) return null
        firstMessageTimestamp = messageTimetoken

        return messageTimetoken?.let { timetoken ->
            if (messageRepository.hasMoreBefore(channelId, timetoken)) null
            else {
                // Get the history // prepend
                val start = timetoken
                val end = null

                MessageWindow(channelId, start, end)
            }
        }
    }

    private suspend fun getTimeWindowForLastMessage(state: PagingState<Int, DBMessage>): MessageWindow? {

        // first page will contains the newest messages
        val message = state.pages.firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.maxByOrNull { it.timetoken }

        // just skip when nothing changed
        if (message?.timetoken == lastMessageTimestamp) return null
        lastMessageTimestamp = message?.timetoken

        return message?.let {
            if (messageRepository.hasMoreAfter(channelId, it.timetoken)) null
            else {
                // Get the history // append
                val start = null// - 1
                val end = it.timetoken + 1
                MessageWindow(channelId, start, end)
            }
        }
    }

    private suspend fun loadNewMessages(channelId: ChannelId, start: Long?, end: Long?) {
        // Get the history
        service.pullHistory(
            channel = channelId,
            start = start,
            end = end,
            count = messageCount,
            withUUID = true,
            withActions = true,
        )
    }
}

private data class MessageWindow(
    val channelId: ChannelId,
    val start: Long?,
    val end: Long?,
)