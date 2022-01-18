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
    fun whenPubNubProviderIsNotUsed_thenAnExceptionIsThrown() = runTest{
        // Given
        composeTestRule.setContent {
            ChannelList(channels = emptyList(), onSelected = {})
        }
    }
//    todo: lazy column items are splitted, not wrapped with parent node
//    @Test
//    fun whenMessagePagingDataWillBePassed_thenItWillBeShown() {
//        // Given
//        val messages = flowOf(PagingData.from(FAKE_DATA))
//        val messageList = context.getString(R.string.message_list)
//        composeTestRule.setContent {
//            PubNubProvider(pubNub = pubNub) {
//                MessageList(messages = messages, onMemberSelected = {})
//            }
//        }
//
//        // Then
//        composeTestRule.onNodeWithContentDescription(messageList).apply {
//            assertIsDisplayed()
//
//            printToLog("MESSAGE_LIST")
//
//            FAKE_DATA.forEachIndexed { index, item ->
//                onChildAt(index).apply {
//                    assertHasClickAction()
//                    printToLog("MESSAGE_LIST")
//                    onChildren().apply {
//
//                        printToLog("MESSAGE_LIST")
//                        assertAny(hasText(item.publisher.name))
//                        assertAny(hasText(item.content.text))
//                    }
//                }
//            }
//        }
//    }
//
//    @Test
//    fun whenMessageMemberIconWillBePressed_thenOnSelectedWillBeCalled() {
//        // Given
//        val messages = flowOf(PagingData.from(FAKE_DATA))
//        val messageList = context.getString(R.string.message_list)
//        val selectedMember = AtomicReference<MemberId>()
//
//        composeTestRule.setContent {
//            PubNubProvider(pubNub = pubNub) {
//                MessageList(messages = messages, onMemberSelected = { println("MESSAGE_LIST, $it"); selectedMember.set(it) })
//            }
//        }
//
//        // Then
//        composeTestRule.onNodeWithContentDescription(messageList, useUnmergedTree = true).apply {
//            assertIsDisplayed()
//
//            FAKE_DATA.forEachIndexed { index, item ->
//                onChildAt(index).performClick()
//                println("MESSAGE_LIST Performed click on index $index, publisher ${item.publisher.id}")
//                Awaitility.await().untilAtomic(selectedMember, Matchers.equalTo(item.publisher.id))
//                selectedMember.set(null)
//            }
//        }
//    }

//    private val FAKE_DATA = listOf(
//        // own, sending, not delivered
//        MessageUi.Data(
//            "id-1",
//            MemberData("user-1", "John Doe", null, "Description 1"),
//            "channel-1",
//            Content("Test Message 1"),
//            1L,
//            true,
//            true,
//            false,
//        ),
//        // own, not sending, not delivered
//        MessageUi.Data(
//            "id-1",
//            MemberData("user-1", "John Doe", null, "Description 1"),
//            "channel-1",
//            Content("Test Message 2"),
//            2L,
//            true,
//            false,
//            false,
//        ),
//        // own, not sending, delivered
//        MessageUi.Data(
//            "id-1",
//            MemberData("user-1", "John Doe", null, "Description 1"),
//            "channel-1",
//            Content("Test Message 3"),
//            3L,
//            true,
//            false,
//            true,
//        ),
//        // not own, sending, not delivered
//        MessageUi.Data(
//            "id-2",
//            MemberData("user-2", "Sissy Cassy", null, "Description 4"),
//            "channel-1",
//            Content("Test Message 4"),
//            4L,
//            true,
//            true,
//            false,
//        ),
//        // not own, not sending, not delivered
//        MessageUi.Data(
//            "id-3",
//            MemberData("user-3", "Doe Joe", null, "Description 5"),
//            "channel-1",
//            Content("Test Message 5"),
//            5L,
//            true,
//            false,
//            false,
//        ),
//        // not own, not sending, delivered
//        MessageUi.Data(
//            "id-4",
//            MemberData("user-4", "Jimmy Hendrix", null, "Description 6"),
//            "channel-1",
//            Content("Test Message 6"),
//            6L,
//            true,
//            false,
//            true,
//        ),
//    )
}
