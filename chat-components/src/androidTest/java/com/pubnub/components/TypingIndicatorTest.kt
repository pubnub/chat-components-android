package com.pubnub.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.pubnub.components.chat.provider.ChatProvider
import com.pubnub.components.chat.ui.component.input.renderer.DefaultTypingIndicatorRenderer
import com.pubnub.framework.data.Typing
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TypingIndicatorTest : BaseTest() {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    override fun setUp() {
        super.setUp()
        mockDatabase()
        mockPubNub()
    }

    @After
    override fun tearDown() {
        super.tearDown()
        unmockDatabase()
        unmockPubNub()
    }

    @Test
    fun whenTypingDataWillBePassed_andTypingWillBeTrue_thenTypingIndicatorWillBeShown() = runTest {
        // Given
        val typingIndicator = context.getString(R.string.typing_indicator)
        val typingData = listOf(Typing("userId", "channelId", true))

        val expectedText = context.getString(
            R.string.is_typing,
            "userId"
        )
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                DefaultTypingIndicatorRenderer.TypingIndicator(data = typingData)
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(typingIndicator, useUnmergedTree = true)
            .apply {
                assertIsDisplayed()
                hasAnyChild(hasText(expectedText))
            }
    }


    @Test
    fun whenTypingDataWillBePassed_andTypingWillBeFalse_thenTypingIndicatorWillNotBeShown() =
        runTest {
            // Given
            val typingIndicator = context.getString(R.string.typing_indicator)
            val typingData = listOf(Typing("userId", "channelId", false))

            val expectedText = context.getString(
                R.string.is_typing,
                "userId"
            )
            composeTestRule.setContent {
                ChatProvider(pubNub = pubNub!!) {
                    DefaultTypingIndicatorRenderer.TypingIndicator(data = typingData)
                }
            }

            // Then
            composeTestRule.onNodeWithContentDescription(typingIndicator, useUnmergedTree = true)
                .apply {
                    assertIsNotDisplayed()
                    onChildren().assertAll(!hasText(expectedText))
                }
        }

    @Test
    fun whenTypingIsShowing_andTypingFalseIsReceived_thenTypingIndicatorWillBeHide() = runTest {
        // Given
        val typingIndicator = context.getString(R.string.typing_indicator)
        val typingData = listOf(Typing("userId", "channelId", true))
        val notTypingData = listOf(Typing("userId", "channelId", false))
        val typingState = mutableStateOf(typingData)

        val expectedText = context.getString(
            R.string.is_typing,
            "userId"
        )
        composeTestRule.setContent {

            ChatProvider(pubNub = pubNub!!) {
                DefaultTypingIndicatorRenderer.TypingIndicator(data = typingState.value)
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(typingIndicator, useUnmergedTree = true)
            .apply {
                assertIsDisplayed()
                hasAnyChild(hasText(expectedText))
            }

        typingState.value = notTypingData
        composeTestRule.onNodeWithContentDescription(typingIndicator, useUnmergedTree = true)
            .apply {
                assertIsNotDisplayed()
                onChildren().assertAll(!hasText(expectedText))
            }
    }

    @Test
    fun whenTypingIsNotShowing_andTypingTrueIsReceived_thenTypingIndicatorWillBeShown() = runTest {
        // Given
        val typingIndicator = context.getString(R.string.typing_indicator)
        val typingData = listOf(Typing("userId", "channelId", true))
        val notTypingData = listOf(Typing("userId", "channelId", false))
        val typingState = mutableStateOf(notTypingData)

        val expectedText = context.getString(
            R.string.is_typing,
            "userId"
        )
        composeTestRule.setContent {

            ChatProvider(pubNub = pubNub!!) {
                DefaultTypingIndicatorRenderer.TypingIndicator(data = typingState.value)
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(typingIndicator, useUnmergedTree = true)
            .apply {
                assertIsNotDisplayed()
                onChildren().assertAll(!hasText(expectedText))
            }

        typingState.value = typingData
        composeTestRule.onNodeWithContentDescription(typingIndicator, useUnmergedTree = true)
            .apply {
                assertIsDisplayed()
                hasAnyChild(hasText(expectedText))
            }
    }
    // rest is tested in MessageInputTest
}