package com.pubnub.components.chat.service.channel

import com.pubnub.api.PubNub
import com.pubnub.api.PubNubException
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.objects.PNPage
import com.pubnub.api.models.consumer.objects.PNSortKey
import com.pubnub.api.models.consumer.pubsub.objects.PNDeleteChannelMetadataEventMessage
import com.pubnub.api.models.consumer.pubsub.objects.PNObjectEventResult
import com.pubnub.api.models.consumer.pubsub.objects.PNSetChannelMetadataEventMessage
import com.pubnub.components.chat.network.mapper.NetworkChannelMapper
import com.pubnub.components.chat.service.error.ErrorHandler
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.repository.channel.ChannelRepository
import com.pubnub.framework.data.ChannelId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(DelicateCoroutinesApi::class)
class DefaultChannelServiceImpl(
    val pubNub: PubNub,
    private val channelRepository: ChannelRepository<DBChannel, DBChannelWithMembers>,
    private val networkMapper: NetworkChannelMapper,
    private val errorHandler: ErrorHandler,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ChannelService<DBChannel> {

    // region Private mutable flows
    private var _totalCount: MutableStateFlow<Int> = MutableStateFlow(-1)
    // endregion

    // region Listener
    private val _listener = object : SubscribeCallback() {
        override fun status(pubnub: PubNub, pnStatus: PNStatus) {
            // empty
        }

        override fun objects(pubnub: PubNub, objectEvent: PNObjectEventResult) {
            super.objects(pubnub, objectEvent)
            handleObject(objectEvent)
        }
    }

    private var subscribedChannels: List<String> = listOf()

    override fun bind(vararg channels: String) {
        subscribedChannels = listOf(*channels)
        pubNub.addListener(_listener)
        pubNub.subscribe(channels = subscribedChannels, withPresence = true)
    }

    override fun unbind() {
        pubNub.unsubscribe(channels = subscribedChannels)
        pubNub.removeListener(_listener)
    }

    override fun getAll(
        limit: Int?,
        page: PNPage?,
        filter: String?,
        sort: Collection<PNSortKey>,
        includeCustom: Boolean,
    ) {
        coroutineScope.launch(dispatcher) {
            getChannels(limit, page, filter, sort, includeCustom) { _page, _ ->
                getAll(limit, _page, filter, sort, includeCustom)
            }
        }
    }

    private suspend fun getChannels(
        limit: Int? = null,
        page: PNPage? = null,
        filter: String? = null,
        sort: Collection<PNSortKey> = listOf(),
        includeCustom: Boolean = false,
        onNext: (PNPage?, Int) -> Unit = { _, _ -> },
    ) {
        try {
            if (_totalCount.value >= 0 && channelRepository.size() >= _totalCount.value) return

            pubNub.getAllChannelMetadata(
                limit = limit,
                page = page,
                filter = filter,
                sort = sort,
                includeCount = true,
                includeCustom = includeCustom,
            ).sync()!!.apply {
                val obtainedPage = prev ?: next
                _totalCount.emit(totalCount!!)

                // repo
                val channelsList = data.toList().map { networkMapper.map(it) }.toTypedArray()
                channelRepository.add(*channelsList)

                onNext(obtainedPage, totalCount!!)
            }

        } catch (e: PubNubException) {
            errorHandler.onError(e)
        }
    }

    override fun add(channel: DBChannel, includeCustom: Boolean) {
        coroutineScope.launch(dispatcher) {
            try {
                pubNub.setChannelMetadata(
                    channel = channel.id,
                    name = channel.name,
                    description = channel.description,
                    custom = channel.custom,
                    includeCustom = includeCustom,
                ).sync()!!
            } catch (e: PubNubException) {
                errorHandler.onError(e)
            }
        }
    }

    override fun remove(id: ChannelId) {
        coroutineScope.launch(dispatcher) {
            try {
                pubNub.removeChannelMetadata(id).sync()!!
            } catch (e: PubNubException) {
                errorHandler.onError(e)
            }
        }
    }

    private fun handleObject(event: PNObjectEventResult) {
        coroutineScope.launch(dispatcher) {
            when (event.extractedMessage) {
                is PNSetChannelMetadataEventMessage -> {
                    val channel = (event.extractedMessage as PNSetChannelMetadataEventMessage).data
                    channelRepository.add(networkMapper.map(channel))
                }
                is PNDeleteChannelMetadataEventMessage -> {
                    val channel =
                        (event.extractedMessage as PNDeleteChannelMetadataEventMessage).channel
                    channelRepository.remove(channel)
                }
                else -> {
                }
            }
        }
    }
}