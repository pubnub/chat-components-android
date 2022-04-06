package com.pubnub.components

import androidx.compose.ui.test.junit4.createComposeRule
import com.pubnub.components.chat.ui.component.channel.ChannelList
import com.pubnub.components.chat.ui.component.provider.MissingPubNubException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MessageListTest : BaseTest() {

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
    fun whenPubNubProviderIsNotUsed_thenAnExceptionIsThrown() = runTest {
        // Given
        composeTestRule.setContent {
            ChannelList(channels = emptyList(), onSelected = {})
        }
    }
}
