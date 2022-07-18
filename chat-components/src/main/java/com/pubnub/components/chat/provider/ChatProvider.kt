package com.pubnub.components.chat.provider

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.components.BuildConfig
import com.pubnub.components.PubNubDatabase
import com.pubnub.components.R
import com.pubnub.components.asPubNub
import com.pubnub.components.chat.network.mapper.*
import com.pubnub.components.chat.service.channel.DefaultChannelService
import com.pubnub.components.chat.service.channel.DefaultOccupancyService
import com.pubnub.components.chat.service.channel.LocalChannelService
import com.pubnub.components.chat.service.channel.LocalOccupancyService
import com.pubnub.components.chat.service.error.TimberErrorHandler
import com.pubnub.components.chat.service.message.DefaultMessageService
import com.pubnub.components.chat.service.message.LocalMessageService
import com.pubnub.components.chat.service.message.action.DefaultMessageReactionService
import com.pubnub.components.chat.service.message.action.LocalActionService
import com.pubnub.components.chat.service.message.action.LocalMessageReactionService
import com.pubnub.components.chat.ui.component.channel.DefaultChannelListTheme
import com.pubnub.components.chat.ui.component.channel.LocalChannelListTheme
import com.pubnub.components.chat.ui.component.input.DefaultLocalMessageInputTheme
import com.pubnub.components.chat.ui.component.input.DefaultTypingIndicatorTheme
import com.pubnub.components.chat.ui.component.input.LocalMessageInputTheme
import com.pubnub.components.chat.ui.component.input.LocalTypingIndicatorTheme
import com.pubnub.components.chat.ui.component.member.DefaultMemberListTheme
import com.pubnub.components.chat.ui.component.member.LocalMemberListTheme
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.menu.DefaultMenuItemTheme
import com.pubnub.components.chat.ui.component.menu.LocalMenuItemTheme
import com.pubnub.components.chat.ui.component.message.*
import com.pubnub.components.chat.ui.component.message.reaction.DefaultReactionTheme
import com.pubnub.components.chat.ui.component.message.reaction.LocalReactionTheme
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import com.pubnub.components.chat.ui.component.provider.LocalUser
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
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.data.message.action.MessageActionDao
import com.pubnub.components.repository.channel.DefaultChannelRepository
import com.pubnub.components.repository.member.DefaultMemberRepository
import com.pubnub.components.repository.membership.DefaultMembershipRepository
import com.pubnub.components.repository.message.DefaultMessageRepository
import com.pubnub.components.repository.message.action.DefaultMessageActionRepository
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.ActionService
import com.pubnub.framework.service.LocalTypingService
import com.pubnub.framework.service.TypingService
import com.pubnub.framework.util.TypingIndicator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@Composable
fun ChatProvider(
    pubNub: PubNub,
    database: PubNubDatabase<MessageDao<DBMessage, DBMessageWithActions>, MessageActionDao<DBMessageAction>, ChannelDao<DBChannel, DBChannelWithMembers>, MemberDao<DBMember, DBMemberWithChannels>, MembershipDao<DBMembership>> = Database.initialize(
        LocalContext.current
    ).asPubNub(),
    channel: ChannelId = "channel.lobby",
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
    val messageActionRepository = DefaultMessageActionRepository(database.actionDao())
    // endregion

    val errorHandler = TimberErrorHandler()

    CompositionLocalProvider(
        LocalPubNub provides pubNub,
        LocalUser provides pubNub.configuration.userId.value,
        LocalChannel provides channel,

        // Themes
        LocalMessageInputTheme provides DefaultLocalMessageInputTheme,
        LocalTypingIndicatorTheme provides DefaultTypingIndicatorTheme,
        LocalChannelListTheme provides DefaultChannelListTheme,
        LocalMemberListTheme provides DefaultMemberListTheme,
        LocalMessageListTheme provides DefaultMessageListTheme,
        LocalMessageTheme provides DefaultLocalMessageTheme,
        LocalReactionTheme provides DefaultReactionTheme,
        LocalIndicatorTheme provides DefaultIndicatorTheme,
        LocalProfileImageTheme provides DefaultProfileImageTheme,
        LocalMenuItemTheme provides DefaultMenuItemTheme,

        // Repositories
        LocalChannelRepository provides channelRepository,
        LocalMessageRepository provides messageRepository,
        LocalMessageActionRepository provides messageActionRepository,
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
    val mapper = LocalPubNub.current.mapper
    val actionService = ActionService(LocalPubNub.current, LocalErrorHandler.current)

    CompositionLocalProvider(
        LocalMessageService provides DefaultMessageService(
            LocalPubNub.current,
            LocalMessageRepository.current,
            LocalMessageActionRepository.current,
            NetworkMessageMapper(mapper),
            NetworkMessageHistoryMapper(mapper),
            NetworkMessageActionHistoryMapper(),
            LocalErrorHandler.current,
        ),
        LocalActionService provides actionService,
        LocalMessageReactionService provides DefaultMessageReactionService(
            LocalUser.current,
            actionService,
            LocalMessageActionRepository.current,
            NetworkMessageActionMapper(),
            LocalErrorHandler.current,
        ),
        LocalChannelService provides DefaultChannelService(
            LocalPubNub.current,
        ),
        LocalTypingService provides TypingService(
            LocalUser.current,
            TypingIndicator(LocalPubNub.current, LocalUser.current),
            LocalErrorHandler.current,
        ),
        LocalOccupancyService provides DefaultOccupancyService(
            LocalPubNub.current,
            LocalUser.current,
            NetworkOccupancyMapper(),
            LocalErrorHandler.current,
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
    val pubNub =
        PubNub(PNConfiguration(com.pubnub.api.UserId("previewUUID")).apply { publishKey = ""; subscribeKey = "" })
    Database.initialize(context)
    ChatProvider(pubNub, synchronize = false) {
        content()
    }
}

@OptIn(FlowPreview::class)
@Composable
fun Synchronize() {
    val currentUser = LocalUser.current

    val channelService = LocalChannelService.current
    val messageService = LocalMessageService.current
    val occupancyService = LocalOccupancyService.current
    val actionService = LocalActionService.current
    val messageActionService = LocalMessageReactionService.current

    val membershipRepository = LocalMembershipRepository.current

    // One instance of disposable!
    DisposableEffect(true) {
        messageService.bind()
        occupancyService.bind()
        actionService.bind()
        messageActionService.bind()

        onDispose {
            messageService.unbind()
            occupancyService.unbind()
            actionService.unbind()
            messageActionService.unbind()
        }
    }


    // Bind for channels
    val channels by membershipRepository.getAll(currentUser).collectAsState(initial = emptyList())
    val errorHandler = LocalErrorHandler.current
    DisposableEffect(channels) {
        val channelArray = channels.map { it.channelId }.toTypedArray()
        errorHandler.i("Bind for channels ${channelArray.joinToString()}")
        // Get current member channels
        channelService.bind(*channelArray)

        onDispose {
            errorHandler.e("Unbind channels")
            channelService.unbind()
        }
    }
}

fun getComponentsSuffix(): Pair<String, String> =
    "Components" to "ACC/${BuildConfig.LIBRARY_VERSION_NAME}"
