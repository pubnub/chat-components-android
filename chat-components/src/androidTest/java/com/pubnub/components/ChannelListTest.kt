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
import kotlinx.coroutines.test.runBlockingTest
import org.awaitility.Awaitility
import org.hamcrest.Matchers
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
    fun whenPubNubProviderIsNotUsed_thenAnExceptionIsThrown() {
        // Given
        composeTestRule.setContent {
            ChannelList(channels = emptyList(), onSelected = {})
        }
    }

    @Test
    fun whenChannelListWillBePassed_thenItWillBeShown() {
        // Given
        val channels = FAKE_CHANNELS
        val channelList =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.channel_list)
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                ChannelList(channels = channels, onSelected = {})
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(channelList, useUnmergedTree = true).apply {
            assertIsDisplayed()

            channels.forEachIndexed { index, item ->
                onChildAt(index).apply {
                    assertHasClickAction()
                    onChildren().apply {
                        assertAny(hasText(item.name))
                        assertAny(hasText(item.description!!))
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun whenChannelPagingDataWillBePassed_thenItWillBeShown() = runBlockingTest {
        // Given
        val channels = flowOf(PagingData.from(FAKE_CHANNELS)) as Flow<PagingData<ChannelUi>>
        val channelList =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.channel_list)

        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                ChannelList(channels = channels, onSelected = {})
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(channelList, useUnmergedTree = true).apply {
            assertIsDisplayed()

            FAKE_CHANNELS.forEachIndexed { index, item ->
                onChildAt(index).apply {
                    assertHasClickAction()
                    onChildren().apply {
                        assertAny(hasText(item.name))
                        assertAny(hasText(item.description!!))
                    }
                }
            }
        }
    }

    @Test
    fun whenChannelWillBePressed_thenOnSelectedWillBeCalled() {
        // Given
        val channels = FAKE_CHANNELS
        val channelList =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.channel_list)

        val selectedChannel = AtomicReference<ChannelUi.Data>()
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                ChannelList(channels = channels, onSelected = { selectedChannel.set(it) })
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(channelList, useUnmergedTree = true).apply {
            assertIsDisplayed()

            channels.forEachIndexed { index, item ->
                onChildAt(index).performClick()
                Awaitility.await().untilAtomic(selectedChannel, Matchers.equalTo(item))
            }
        }
    }

    private val FAKE_CHANNELS = listOf(
        ChannelUi.Data(
            "channel-1",
            "Channel 1",
            "Description 1",
            ChannelUi.Data.DEFAULT,
            "url",
            null,
            emptyList()
        ),
        ChannelUi.Data(
            "channel-2",
            "Channel 2",
            "Description 2",
            ChannelUi.Data.DEFAULT,
            "url",
            null,
            emptyList()
        ),
        ChannelUi.Data(
            "channel-3",
            "Channel 3",
            "Description 3",
            ChannelUi.Data.DEFAULT,
            "url",
            null,
            emptyList()
        ),
    )
}
