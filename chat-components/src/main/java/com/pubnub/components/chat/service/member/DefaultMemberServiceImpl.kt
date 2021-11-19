package com.pubnub.components.chat.service.member

import com.pubnub.api.PubNub
import com.pubnub.api.PubNubException
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.objects.PNPage
import com.pubnub.api.models.consumer.objects.PNSortKey
import com.pubnub.api.models.consumer.pubsub.objects.PNDeleteUUIDMetadataEventMessage
import com.pubnub.api.models.consumer.pubsub.objects.PNObjectEventResult
import com.pubnub.api.models.consumer.pubsub.objects.PNSetUUIDMetadataEventMessage
import com.pubnub.components.chat.network.mapper.NetworkMemberMapper
import com.pubnub.components.chat.service.error.ErrorHandler
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DBMemberWithChannels
import com.pubnub.components.repository.member.MemberRepository
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(DelicateCoroutinesApi::class)
class DefaultMemberServiceImpl(
    private val pubNub: PubNub,
    private val memberRepository: MemberRepository<DBMember, DBMemberWithChannels>,
    private val networkMapper: NetworkMemberMapper,
    private val errorHandler: ErrorHandler,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MemberService<DBMember> {

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
            getMembers(limit, page, filter, sort, includeCustom) { _page, _ ->
                getAll(limit, _page, filter, sort, includeCustom)
            }
        }
    }

    private suspend fun getMembers(
        limit: Int? = null,
        page: PNPage? = null,
        filter: String? = null,
        sort: Collection<PNSortKey> = listOf(),
        includeCustom: Boolean = false,
        onNext: (PNPage?, Int) -> Unit = { _, _ -> },
    ) {
        try {
            if (_totalCount.value >= 0 && memberRepository.size() >= _totalCount.value) return

            pubNub.getAllUUIDMetadata(
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
                val membersList = data.toList().map { networkMapper.map(it) }.toTypedArray()
                memberRepository.add(*membersList)

                onNext(obtainedPage, totalCount!!)
            }

        } catch (e: PubNubException) {
            errorHandler.onError(e)
        }
    }

    override fun add(member: DBMember, includeCustom: Boolean) {
        coroutineScope.launch(dispatcher) {
            try {
                pubNub.setUUIDMetadata(
                    uuid = member.id,
                    name = member.name,
                    externalId = member.externalId,
                    profileUrl = member.profileUrl,
                    email = member.email,
                    custom = member.custom,
                    includeCustom = includeCustom,
                ).sync()!!
            } catch (e: PubNubException) {
                errorHandler.onError(e)
            }
        }
    }

    override fun remove(id: UserId?) {
        coroutineScope.launch(dispatcher) {
            id?.let { memberId ->
                try {
                    pubNub.removeUUIDMetadata(memberId).sync()!!
                } catch (e: PubNubException) {
                    errorHandler.onError(e)
                }
            }
        }
    }

    private fun handleObject(event: PNObjectEventResult) {
        coroutineScope.launch(dispatcher) {
            when (event.extractedMessage) {
                is PNSetUUIDMetadataEventMessage -> {
                    val member = (event.extractedMessage as PNSetUUIDMetadataEventMessage).data
                    memberRepository.add(networkMapper.map(member))
                }
                is PNDeleteUUIDMetadataEventMessage -> {
                    val member = (event.extractedMessage as PNDeleteUUIDMetadataEventMessage).uuid
                    memberRepository.remove(member)
                }
                else -> {
                }
            }
        }
    }
}