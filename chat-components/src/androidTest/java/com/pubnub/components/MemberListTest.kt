package com.pubnub.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.paging.PagingData
import androidx.test.platform.app.InstrumentationRegistry
import com.pubnub.components.chat.provider.ChatProvider
import com.pubnub.components.chat.ui.component.member.MemberList
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.components.chat.ui.component.provider.MissingPubNubException
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class MemberListTest : BaseTest() {

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
            MemberList(members = emptyList())
        }
    }

    @Test
    fun whenMemberListWillBePassed_thenItWillBeShown() {
        // Given
        val members = FAKE_MEMBERS
        val memberList =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.member_list)
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MemberList(members = members)
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(memberList, useUnmergedTree = true).apply {
            assertIsDisplayed()

            members.forEachIndexed { index, item ->
                onChildAt(index).apply {
                    assertHasClickAction()
                    onChildren().apply {
                        assertAny(hasText(item.name))
                        assertAny(hasText(item.description))
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun whenMemberPagingDataWillBePassed_thenItWillBeShown() = runBlockingTest {
        // Given
        val members = flowOf(PagingData.from(FAKE_MEMBERS) as PagingData<MemberUi>)
        val memberList =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.member_list)

        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MemberList(members = members)
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(memberList, useUnmergedTree = true).apply {
            assertIsDisplayed()

            FAKE_MEMBERS.forEachIndexed { index, item ->
                onChildAt(index).apply {
                    assertHasClickAction()
                    onChildren().apply {
                        assertAny(hasText(item.name))
                        assertAny(hasText(item.description))
                    }
                }
            }
        }
    }

    @Test
    fun whenMemberWillBePressed_thenOnSelectedWillBeCalled() {
        // Given
        val members = FAKE_MEMBERS
        val memberList =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.member_list)

        val selectedMember = AtomicReference<MemberUi.Data>()
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MemberList(members = members, onSelected = { selectedMember.set(it) })
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(memberList, useUnmergedTree = true).apply {
            assertIsDisplayed()

            members.forEachIndexed { index, item ->
                onChildAt(index).performClick()
                Awaitility.await().untilAtomic(selectedMember, Matchers.equalTo(item))
            }
        }
    }

    // region Presence
    @Test
    fun whenMemberListWillBePassedWithoutPresenceObject_thenPresenceIndicatorWillNotBeShown() {
        // Given
        val members = FAKE_MEMBERS
        val memberList =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.member_list)
        val online =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.online_presence_indicator_member_list)
        val offline =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.offline_presence_indicator_member_list)
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MemberList(members = members, presence = null)
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(memberList, useUnmergedTree = true).apply {
            members.forEachIndexed { index, _ ->
                onChildAt(index).apply {
                    onChildren().apply {
                        assert(!hasContentDescription(online))
                        assert(!hasContentDescription(offline))
                    }
                }
            }
        }
    }

    @Test
    fun whenMemberListWillBePassedWithPresenceObject_thenPresenceIndicatorWillBeShown() {
        // Given
        val members = FAKE_MEMBERS
        val memberList =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.member_list)
        val online =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.online_presence_indicator_member_list)
        val offline =
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.offline_presence_indicator_member_list)

        val fakePresence = Presence().apply {
            add("user1", true)
            add("user2", false)
            add("user3", true)
        }
        composeTestRule.setContent {
            ChatProvider(pubNub = pubNub!!) {
                MemberList(members = members, presence = fakePresence)
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(memberList, useUnmergedTree = true).apply {
            members.forEachIndexed { index, item ->
                onChildAt(index).apply {
                    // Profile Image
                    onChildAt(0).apply {
                        onChildren().apply {
                            if (fakePresence.get(item.id).value) assertAny(
                                hasContentDescription(
                                    online
                                )
                            )
                            else assertAny(hasContentDescription(offline))
                        }
                    }
                }
            }
        }
    }
    // endregion

    private val FAKE_MEMBERS = listOf(
        MemberUi.Data("user1", "User Nr 1", null, "Fake Account 1"),
        MemberUi.Data("user2", "User Nr 2", null, "Fake Account 2"),
        MemberUi.Data("user3", "User Nr 3", null, "Fake Account 3"),
    )
}
