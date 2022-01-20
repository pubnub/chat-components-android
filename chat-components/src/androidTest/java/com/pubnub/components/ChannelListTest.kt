package com.pubnub.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.paging.PagingData
import androidx.test.platform.app.InstrumentationRegistry
import com.pubnub.components.chat.provider.ChatProvider
import com.pubnub.components.chat.ui.component.channel.ChannelList
import com.pubnub.components.chat.ui.component.channel.ChannelUi
import com.pubnub.components.chat.ui.component.provider.MissingPubNubException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference

@OptIn(ExperimentalCoroutinesApi::class)
class ChannelListTest : BaseTest() {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    override fun setUp() {
        super.setUp()
        mockPubNub()
        mockDatabase()
    }

    @After
    override fun tearDown() {
        super.tearDown()
        unmockDatabase()
        unmockPubNub()
    }

    @Test(expected = MissingPubNubException::class)
    fun whenPubNubProviderIsNotUsed_thenAnExceptionIsThrown() = runTest{
        // Given
        composeTestRule.setContent {
            ChannelList(channels = emptyList(), onSelected = {})
        }
    }

    @Test
    fun whenChannelListWillBePassed_thenItWillBeShown() = runTest{
        // Given
        val channels = FAKE_CHANNELS
        val channelList = context.getString(R.string.channel_list)
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                ChannelList(channels = channels, onSelected = {})
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(channelList, useUnmergedTree = true).apply {
            assertIsDisplayed()

            channels.forEachIndexed { index, item ->
                // Thumbnail
                onChildAt(3 * index).assertContentDescriptionEquals(context.getString(R.string.thumbnail))
                // Channel name
                onChildAt(3 * index + 1).assert(hasText(item.name))
                // Channel description
                onChildAt(3 * index + 2).assert(hasText(item.description!!))
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun whenChannelPagingDataWillBePassed_thenItWillBeShown() = runTest {
        // Given
        val channels = flowOf(PagingData.from(FAKE_CHANNELS)) as Flow<PagingData<ChannelUi>>
        val channelList = context.getString(R.string.channel_list)

        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                ChannelList(channels = channels, onSelected = {})
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(channelList, useUnmergedTree = false).apply {
            assertIsDisplayed()

            FAKE_CHANNELS.forEachIndexed { index, item ->
                // Thumbnail
                onChildAt(3 * index).assertContentDescriptionEquals(context.getString(R.string.thumbnail))
                // Channel name
                onChildAt(3 * index + 1).assert(hasText(item.name))
                // Channel description
                onChildAt(3 * index + 2).assert(hasText(item.description!!))
            }
        }
    }

    private val FAKE_CHANNELS = listOf(
        ChannelUi.Data(
            id = "channel-1",
            name = "Channel 1",
            description ="Description 1",
            type = ChannelUi.Data.DEFAULT,
            profileUrl = "url",
            members = emptyList()
        ),
        ChannelUi.Data(
            id = "channel-2",
            name = "Channel 2",
            description = "Description 2",
            type = ChannelUi.Data.DEFAULT,
            profileUrl = "url",
            members = emptyList()
        ),
        ChannelUi.Data(
            id ="channel-3",
            name = "Channel 3",
            description = "Description 3",
            type = ChannelUi.Data.DEFAULT,
            profileUrl = "url",
            members = emptyList()
        ),
    )
}
