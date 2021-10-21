package com.pubnub.components.chat.provider

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.components.BuildConfig
import com.pubnub.components.PubNubDatabase
import com.pubnub.components.R
import com.pubnub.components.chat.network.mapper.*
import com.pubnub.components.chat.service.channel.DefaultChannelServiceImpl
import com.pubnub.components.chat.service.channel.LocalChannelService
import com.pubnub.components.chat.service.channel.LocalOccupancyService
import com.pubnub.components.chat.service.channel.OccupancyService
import com.pubnub.components.chat.service.error.TimberErrorHandler
import com.pubnub.components.chat.service.member.DefaultMemberServiceImpl
import com.pubnub.components.chat.service.member.LocalMemberService
import com.pubnub.components.chat.service.message.DefaultMessageServiceImpl
import com.pubnub.components.chat.service.message.LocalMessageService
import com.pubnub.components.chat.ui.component.channel.DefaultChannelListTheme
import com.pubnub.components.chat.ui.component.channel.LocalChannelListTheme
import com.pubnub.components.chat.ui.component.input.DefaultLocalMessageInputTheme
import com.pubnub.components.chat.ui.component.input.DefaultTypingIndicatorTheme
import com.pubnub.components.chat.ui.component.input.LocalMessageInputTheme
import com.pubnub.components.chat.ui.component.input.LocalTypingIndicatorTheme
import com.pubnub.components.chat.ui.component.member.DefaultMemberListTheme
import com.pubnub.components.chat.ui.component.member.LocalMemberListTheme
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.message.*
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import com.pubnub.components.chat.ui.mapper.member.DBMemberMapper
import com.pubnub.components.data.Database
import com.pubnub.components.data.channel.ChannelDao
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DBMemberWithChannels
import com.pubnub.components.data.member.MemberDao
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.components.data.membership.MembershipDao
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.MessageDao
import com.pubnub.components.repository.channel.DefaultChannelRepository
import com.pubnub.components.repository.member.DefaultMemberRepository
import com.pubnub.components.repository.membership.DefaultMembershipRepository
import com.pubnub.components.repository.message.DefaultMessageRepository

import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.LocalTypingService
import com.pubnub.framework.service.TypingService
import com.pubnub.framework.util.TypingIndicator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import timber.log.Timber

const val DEFAULT_CHANNEL = "channel.lobby"

@Composable
fun ChatProvider(
    pubNub: PubNub,
    database: PubNubDatabase<MessageDao<DBMessage, DBMessage>, ChannelDao<DBChannel, DBChannelWithMembers>, MemberDao<DBMember, DBMemberWithChannels>, MembershipDao<DBMembership>> = Database.INSTANCE,
    channel: String = DEFAULT_CHANNEL,
    synchronize: Boolean = true,
    content: @Composable() () -> Unit,
) {
    // Set PNSDK suffix
    @Suppress("DEPRECATION")
    pubNub.configuration.addPnsdkSuffix(getComponentsSuffix())

    // region Member part
    val unknownMemberTitle = stringResource(id = R.string.member_unknown_title)
    val unknownMemberDescription = stringResource(id = R.string.member_unknown_description)

    val memberDbMapper = DBMemberMapper()
    val memberRepository =
        DefaultMemberRepository(database.memberDao())
    val memberFormatter: (UserId) -> MemberUi.Data = { id ->
        runBlocking {
            (memberRepository.get(id)?.let { memberDbMapper.map(it) } ?: MemberUi.Data(
                id,
                unknownMemberTitle,
                null,
                unknownMemberDescription,
            ))
        }
    }
    // endregion

    // region Membership
    val membershipRepository = DefaultMembershipRepository(database.membershipDao())
    // endregion

    // region Channel
    val channelRepository = DefaultChannelRepository(database.channelDao())
    // endregion

    // region Message
    val messageRepository =
        DefaultMessageRepository(database.messageDao())
    // endregion

    val errorHandler = TimberErrorHandler()

    CompositionLocalProvider(
        LocalPubNub provides pubNub,
        LocalChannel provides channel,

        // Themes
        LocalMessageInputTheme provides DefaultLocalMessageInputTheme,
        LocalTypingIndicatorTheme provides DefaultTypingIndicatorTheme,
        LocalChannelListTheme provides DefaultChannelListTheme,
        LocalMemberListTheme provides DefaultMemberListTheme,
        LocalMessageListTheme provides DefaultMessageListTheme,
        LocalMessageTheme provides DefaultLocalMessageTheme,
        LocalIndicatorTheme provides DefaultIndicatorTheme,
        LocalProfileImageTheme provides DefaultProfileImageTheme,

        // Repositories
        LocalChannelRepository provides channelRepository,
        LocalMessageRepository provides messageRepository,
        LocalMemberRepository provides memberRepository,
        LocalMembershipRepository provides membershipRepository,

        // Utils
        LocalMemberFormatter provides memberFormatter,
        LocalErrorHandler provides errorHandler,

        ) {
        WithServices(synchronize) {
            content()
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun WithServices(
    sync: Boolean,
    content: @Composable() () -> Unit
) {
    val memberFormatter = LocalMemberFormatter.current
    val mapper = LocalPubNub.current.mapper
    val usernameResolver: (UserId) -> String = { id ->
        runBlocking {
            memberFormatter(id)
        }.name
    }

    CompositionLocalProvider(
        LocalMessageService provides DefaultMessageServiceImpl(
            LocalPubNub.current,
            LocalMessageRepository.current,
            NetworkMessageMapper(mapper),
            NetworkMessageHistoryMapper(mapper),
            LocalErrorHandler.current,
        ),
        LocalChannelService provides DefaultChannelServiceImpl(
            LocalPubNub.current,
            LocalChannelRepository.current,
            NetworkChannelMapper(mapper),
            LocalErrorHandler.current,
        ),
        LocalMemberService provides DefaultMemberServiceImpl(
            LocalPubNub.current,
            LocalMemberRepository.current,
            NetworkMemberMapper(mapper),
            LocalErrorHandler.current,
        ),
        LocalTypingService provides TypingService(
            LocalPubNub.current.configuration.uuid,
            usernameResolver,
            TypingIndicator(LocalPubNub.current),
        ),
        LocalOccupancyService provides OccupancyService(
            LocalPubNub.current,
            NetworkOccupancyMapper(),
        ),
    ) {
        if (sync) Synchronize()
        content()
    }
}

@Composable
fun PubNubPreview(
    content: @Composable() () -> Unit,
) {
    val context = LocalContext.current
    val pubNub = PubNub(PNConfiguration().apply { publishKey = ""; subscribeKey = "" })
    Database.initialize(context)
    ChatProvider(pubNub) {
        content()
    }
}

@OptIn(FlowPreview::class)
@Composable
fun Synchronize() {
    val currentUser = LocalPubNub.current.configuration.uuid

    val channelService = LocalChannelService.current
    val messageService = LocalMessageService.current
    val occupancyService = LocalOccupancyService.current

    val membershipRepository = LocalMembershipRepository.current

    // One instance of disposable!
    DisposableEffect(true) {
        Timber.e("ChannelEffect bind")
        messageService.bind()
        occupancyService.bind()

        onDispose {
            Timber.e("ChannelEffect unbind")
            messageService.unbind()
            occupancyService.unbind()
        }
    }


    // Bind for channels
    val channels by membershipRepository.getAll(currentUser).collectAsState(initial = emptyList())
    DisposableEffect(channels) {
        val channelArray = channels.map { it.channelId }.toTypedArray()
        Timber.e("Bind for channels ${channelArray.joinToString()}")
        // Get current member channels
        channelService.bind(*channelArray)

        onDispose {
            Timber.e("Unbind channels")
            channelService.unbind()
        }
    }
}

fun getComponentsSuffix(): Pair<String, String> =
    "Components" to "ACC/${BuildConfig.LIBRARY_VERSION_NAME}"
