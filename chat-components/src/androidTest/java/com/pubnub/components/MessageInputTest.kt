package com.pubnub.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.pubnub.api.PubNubError
import com.pubnub.api.PubNubException
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.components.chat.provider.ChatProvider
import com.pubnub.components.chat.service.message.DefaultMessageServiceImpl
import com.pubnub.components.chat.service.message.LocalMessageService
import com.pubnub.components.chat.service.message.MessageServiceNotInitializedException
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.input.LocalMessageInputTheme
import com.pubnub.components.chat.ui.component.input.MessageInput
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import com.pubnub.components.chat.ui.component.provider.MissingChannelException
import com.pubnub.components.chat.ui.component.provider.MissingPubNubException
import com.pubnub.framework.service.LocalTypingService
import com.pubnub.framework.service.TypingServiceNotInitializedException
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.data.PNException
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.awaitility.Awaitility
import org.hamcrest.Matchers
import org.junit.*
import java.util.concurrent.atomic.AtomicReference

@OptIn(ExperimentalCoroutinesApi::class)
class MessageInputTest : BaseTest() {

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

    @Test(expected = MessageServiceNotInitializedException::class)
    fun whenMessageServiceIsNotInitialized_thenAnExceptionIsThrown() = runTest{
        // Given
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalChannel provides "channel",
                LocalTypingService provides mockk(),
                LocalMessageInputTheme provides ThemeDefaults.messageInput(),
            ) {
                MessageInput()
            }
        }
    }

    @Test(expected = TypingServiceNotInitializedException::class)
    fun whenTypingServiceIsNotProvided_andTypingIndicatorIsTrue_thenAnExceptionIsThrown() = runTest{
        // Given
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalChannel provides "channel",
                LocalPubNub provides pubNub!!,
                LocalMessageService provides mockk(),
            ) {
                MessageInput(typingIndicator = true)
            }
        }
    }

    @Test
    fun whenTypingServiceIsNotProvided_andTypingIndicatorIsFalse_thenNoExceptionIsThrown() = runTest{
        // Given
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MessageInput(typingIndicator = true)
            }
        }
    }

    @Test(expected = MessageServiceNotInitializedException::class)
    fun whenMessageServiceIsNotProvided_thenAnExceptionIsThrown() = runTest{
        // Given
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalChannel provides "channel",
                LocalTypingService provides mockk(),
                LocalPubNub provides mockk(),
            ) {
                MessageInput()
            }
        }
    }

    @Test(expected = MissingChannelException::class)
    fun whenChannelIsNotProvided_thenAnExceptionIsThrown() = runTest{
        // Given
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalPubNub provides mockk(),
                LocalMessageService provides mockk(),
            ) {
                MessageInput()
            }
        }
    }

    @Test(expected = MissingPubNubException::class)
    fun whenPubNubProviderIsNotUsed_thenAnExceptionIsThrown() = runTest{
        // Given
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalChannel provides "channel",
                LocalTypingService provides mockk(),
                LocalMessageService provides mockk(),
            ) {
                MessageInput()
            }
        }
    }

    @Test
    fun whenPlaceholderWillBePassed_thenItWillBeShown() = runTest{
        // Given
        val placeholder = "Text Placeholder"
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MessageInput(placeholder = placeholder)
            }
        }

        // Then
        composeTestRule.onNodeWithText(placeholder).assertIsDisplayed()
    }

    @Test
    fun whenInitialTextWillBePassed_thenItWillBeShown() = runTest{
        // Given
        val text = "Initial text"
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MessageInput(initialText = text)
            }
        }

        // Then
        composeTestRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun whenSendButtonWillBePressed_thenOnSendWithMessageAsParameterWillBeInvokedAndFieldWillBeCleared() = runTest{
        // Given
        val inputDescription =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.message_input_text)
        val buttonDescription =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.message_input_button)
        val text = "Initial text"
        val sentMessage = AtomicReference<String>()//mutableStateOf("")
        val onSend: (String, Timetoken) -> Unit = { message, _ -> sentMessage.set(message) }

        every {
            pubNub!!.publish(allAny(), allAny()).async(allAny())
        } answers {
            firstArg<(PNPublishResult, PNStatus) -> Unit>().invoke(
                pnPublishResult,
                pnStatusSuccess
            )
        }

        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MessageInput(initialText = text, onSuccess = onSend)
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription(inputDescription).assertTextEquals(text)
        composeTestRule.onNodeWithContentDescription(buttonDescription).performClick()

        Awaitility.await().untilAtomic(sentMessage, Matchers.equalTo(text))

        // Then
        Assert.assertEquals(text, sentMessage.get())
        composeTestRule.onNodeWithContentDescription(inputDescription, useUnmergedTree = true)
            .assertTextEquals("")
    }

    @Test
    fun whenSendButtonWillBePressedAndMessageIsNotEmpty_thenMessageWillBeSent() = runTest{
        // Given
        val buttonDescription =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.message_input_button)
        val text = "Initial text"
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MessageInput(initialText = text)
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription(buttonDescription).performClick()

        // Then
        verify(exactly = 1) { pubNub!!.publish(allAny(), allAny()) }
    }

    @Test
    fun whenSendButtonWillBePressedAndMessageIsEmpty_thenMessageWillNotBeSent() = runTest{
        // Given
        val buttonDescription =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.message_input_button)
        val text = ""
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MessageInput(initialText = text)
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription(buttonDescription).performClick()

        // Then
        verify(exactly = 0) { pubNub!!.publish(any(), any()) }
    }

    @Test
    fun whenMessageWillBeSent_thenMessageServiceSendWillBeCalled() = runTest{
        // Given
        val messageService = mockk<DefaultMessageServiceImpl>(relaxed = true)
        val buttonDescription =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.message_input_button)
        val text = "Initial text"
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                CompositionLocalProvider(LocalMessageService provides messageService) {
                    MessageInput(initialText = text)
                }
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription(buttonDescription).performClick()

        // Then
        coVerify(exactly = 1) { messageService.send(allAny(), allAny()) }
    }

    @Test
    fun whenMessageWillBeSent_andResultWillBeSuccess_thenOnMessageWillBeInvoked() = runTest{
        // Given
        val inputDescription =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.message_input_text)
        val buttonDescription =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.message_input_button)
        val text = "Initial text"
        val sentMessage = AtomicReference<String>()//mutableStateOf("")
        val onSend: (String, Timetoken) -> Unit = { message, _ -> sentMessage.set(message) }

        every {
            pubNub!!.publish(allAny(), allAny()).async(allAny())
        } answers {
            firstArg<(PNPublishResult, PNStatus) -> Unit>().invoke(
                pnPublishResult,
                pnStatusSuccess
            )
        }

        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MessageInput(initialText = text, onSuccess = onSend)
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription(inputDescription).assertTextEquals(text)
        composeTestRule.onNodeWithContentDescription(buttonDescription).performClick()

        // Then
        Awaitility.await().untilAtomic(sentMessage, Matchers.equalTo(text))
    }

    @Test
    fun whenMessageWillBeSent_andResultWillBeFailure_thenOnErrorWillBeInvoked() = runTest{
        // Given
        val inputDescription =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.message_input_text)
        val buttonDescription =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.message_input_button)
        val text = "Initial text"
        val sentMessage = AtomicReference<String>()
        val error = AtomicReference<Exception>()
        val onSend: (String, Timetoken) -> Unit = { message, _ -> sentMessage.set(message) }
        val onError: (Exception) -> Unit = { error.set(it) }

        every {
            pubNub!!.publish(allAny(), allAny()).async(allAny())
        } answers {
            firstArg<(PNPublishResult, PNStatus) -> Unit>().invoke(
                pnPublishResult,
                pnException.status
            )
        }

        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MessageInput(initialText = text, onSuccess = onSend, onError = onError)
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription(inputDescription).assertTextEquals(text)
        composeTestRule.onNodeWithContentDescription(buttonDescription).performClick()

        // Then
        Awaitility.await().untilAtomic(error, Matchers.notNullValue())
        Awaitility.await().untilAtomic(sentMessage, Matchers.nullValue())
    }

    val pnPublishResult: PNPublishResult = mockk<PNPublishResult>(relaxed = true).apply {
        every { timetoken } returns 1L
    }

    val pnStatusSuccess = PNStatus(
        category = PNStatusCategory.PNAcknowledgmentCategory,
        error = false,
        operation = PNOperationType.PNPublishOperation,
        exception = null,
        statusCode = 200,
        tlsEnabled = true,
        origin = "ps.pndsn.com",
        uuid = "pn-b092f488-4601-4a08-b469-f58f222f8bc8",
        authKey = null,
        affectedChannels = listOf("lobby"),
        affectedChannelGroups = emptyList(),
    )
    val pnException = PNException(
        exception = PubNubException(
            errorMessage = "UnknownHostException: Unable to resolve host \"ps.pndsn.com\": No address associated with hostname",
            pubnubError = PubNubError.CONNECTION_NOT_SET,
            jso = null,
            statusCode = 0,
            affectedCall = null
        ),
        status = PNStatus(
            category = PNStatusCategory.PNUnexpectedDisconnectCategory,
            error = true,
            operation = PNOperationType.PNPublishOperation,
            exception = PubNubException(
                errorMessage = "UnknownHostException: Unable to resolve host \"ps.pndsn.com\": No address associated with hostname",
                pubnubError = PubNubError.CONNECTION_NOT_SET,
                jso = null,
                statusCode = 0,
                affectedCall = null
            ),
            statusCode = null,
            tlsEnabled = null,
            origin = null,
            uuid = null,
            authKey = null,
            affectedChannels = listOf("lobby"),
            affectedChannelGroups = emptyList(),
        )
    )
}