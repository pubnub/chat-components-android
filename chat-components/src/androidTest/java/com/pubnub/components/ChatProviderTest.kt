package com.pubnub.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import com.pubnub.components.chat.provider.ChatProvider
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.input.DefaultLocalMessageInputTheme
import com.pubnub.components.chat.ui.component.input.LocalMessageInputTheme
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class ChatProviderTest : BaseTest() {

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

    @Test
    fun whenPubNubIsInitialized_thenLocalPubNubReturnsTheInstance() = runTest {
        // Given
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                // Then
                Assert.assertEquals(pubNub, LocalPubNub.current)
            }
        }
    }

    @Test
    fun whenMessageInputThemeIsNotProvided_thenLocalCompositionReturnsTheDefaultInstance() = runTest {
        // Given
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                // Then
                Assert.assertEquals(DefaultLocalMessageInputTheme, LocalMessageInputTheme.current)
            }
        }
    }

    @Test
    fun whenMessageInputThemeIsProvided_thenLocalCompositionReturnsThePassedInstance() = runTest {
        // Given
        composeTestRule.setContent {

            val messageInputTheme = ThemeDefaults.messageInput(modifier = Modifier)
            ChatProvider(pubNub = pubNub!!) {
                CompositionLocalProvider(LocalMessageInputTheme provides messageInputTheme) {
                    // Then
                    Assert.assertEquals(messageInputTheme, LocalMessageInputTheme.current)
                    Assert.assertNotEquals(
                        DefaultLocalMessageInputTheme,
                        LocalMessageInputTheme.current
                    )
                }
            }
        }
    }
}