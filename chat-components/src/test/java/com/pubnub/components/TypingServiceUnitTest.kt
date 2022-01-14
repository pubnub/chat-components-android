package com.pubnub.components

import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.Typing
import com.pubnub.framework.data.TypingMap
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.TypingService
import com.pubnub.framework.util.TypingIndicator
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.*
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class TypingServiceUnitTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun plant() {
            Timber.plant(TestTree())
        }

        @AfterClass
        @JvmStatic
        fun remove() {
            Timber.uprootAll()
        }
    }

    private val testCoroutineDispatcher = UnconfinedTestDispatcher()

    private val testCoroutineScope = TestScope(testCoroutineDispatcher)


    private val userId: UserId = "currentUserId"
    private val usernameResolver: (UserId) -> String = { "username_$it" }

    private val typingIndicator: TypingIndicator =
        spyk(TypingIndicator(mockk()), recordPrivateCalls = true)

    lateinit var service: TypingService

    @Before
    fun setup() {
        MockKAnnotations.init(
            this,
            relaxed = true,
            relaxUnitFun = true,
            overrideRecordPrivateCalls = true
        )
        service = spyk(
            TypingService(
                userId,
                usernameResolver,
                typingIndicator,
                testCoroutineScope,
                testCoroutineDispatcher,
            ), recordPrivateCalls = true
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // region Binding
    @Test
    fun whenBindIsCalled_thenListeningForSignalIsStarted() {
        val channelId: ChannelId = "fakeChannel"
        service.bind(channelId)

        verify(exactly = 1) { service["listenForSignal"]() }
    }

    @Test
    fun whenBindIsCalled_thenTimeoutTimerIsStarted() {
        val channelId: ChannelId = "fakeChannel"
        service.bind(channelId)

        verify(exactly = 1) { service["startTimeoutTimer"]() }
    }

    @Test
    fun whenUnbindIsCalled_thenListeningForSignalIsRemoved() {
        val channelId: ChannelId = "fakeChannel"
        service.bind(channelId)
        service.unbind()
        verify(exactly = 1) { service["listenForSignal"]() }
        verify(exactly = 1) { service["stopListenForPresence"]() }
    }

    @Test
    fun whenUnbindIsCalled_thenTimeoutTimerIsRemoved() {
        val channelId: ChannelId = "fakeChannel"
        service.bind(channelId)
        service.unbind()

        verify(exactly = 1) { service["startTimeoutTimer"]() }
        verify(exactly = 1) { service["stopTimeoutTimer"]() }
    }
    // endregion

    // region Listeners

    @Test
    fun whenListenForSignalIsCalled_thenGetTypingIsExecuted() {
        val channelId: ChannelId = "fakeChannel"
        service.bind(channelId)

        verify(exactly = 1) { typingIndicator.getTyping() }
    }

    @Test
    fun whenListenForSignalIsCalled_andResultIsAvailable_thenSetTypingDataIsExecuted() {
        val channelId: ChannelId = "fakeChannel"
        val fakeTyping = Typing("userId", channelId, true)
        val typingFlow: Flow<Typing> = flowOf(fakeTyping)

        every { typingIndicator.getTyping() } answers { typingFlow }
        service.bind(channelId)


        verify(exactly = 1) { service["setTypingData"](eq(fakeTyping)) }
    }

    @Test
    fun whenStopListenForPresenceIsCalled_thenPresenceIsNotUpdatedAnymore() {
        val channelId: ChannelId = "fakeChannel"
        val fakeTyping = Typing("userId", channelId, true)
        val fakeTyping2 = Typing("userId2", channelId, true)
        val typingFlow: Flow<Typing> = flow {
            this.emit(fakeTyping)
            delay(10_000L)
            this.emit(fakeTyping2)
        }

        every { typingIndicator.getTyping() } answers { typingFlow }
        service.bind(channelId)

        verify(exactly = 1) { service["setTypingData"](eq(fakeTyping)) }
//        testCoroutineScope.advanceUntilIdle()
        service.unbind()

        verify(exactly = 0, timeout = 10_000L) { service["setTypingData"](eq(fakeTyping2)) }
    }
    // endregion

    // region Timeout
    @Test
    fun whenStartTimeoutTimerIsCalled_thenRemoveOutdatedIsCalledEverySecond() {
        val channelId: ChannelId = "fakeChannel"
        service.bind(channelId)

        verify(atLeast = 10, timeout = 10_000L) { service["removeOutdated"]() }
    }

    @Test
    fun whenStopTimeoutTimerIsCalled_thenRemoveOutdatedIsNotCalledAnymore() {
        val channelId: ChannelId = "fakeChannel"
        service.bind(channelId)
        verify(atMost = 1) { service["removeOutdated"]() }
        service.unbind()
        verify(atMost = 1, timeout = 10_000L) { service["removeOutdated"]() }
    }
    // endregion


    // region Outdated

    @Test
    fun whenRemoveOutdatedIsCalled_andTypingItemTimestampIsOlderThan5SecondsAgo_thenTypingIsSetToFalse() {
        val channelId: ChannelId = "fakeChannel"
        val fakeTyping = Typing("userId", channelId, true)

        every { service["getTypingMap"]() } answers { hashMapOf(fakeTyping.userId to fakeTyping) }
        service.bind(channelId)

        val typing: CapturingSlot<Typing> = CapturingSlot()
        verify(exactly = 1, timeout = 6_000L) { service["setTypingData"](capture(typing)) }

        with(typing.captured) {
            Assert.assertEquals(fakeTyping.userId, userId)
            Assert.assertEquals(fakeTyping.channelId, channelId)
            Assert.assertFalse(isTyping)
            Assert.assertTrue(fakeTyping.timestamp + 50_000L < timestamp)
        }
    }

    @Test
    fun whenSetTypingDataIsCalled_andUserIsNotOnList_thenEmitNewDataIsExecuted() {
        val channelId: ChannelId = "fakeChannel"
        val fakeTyping = Typing("userId", channelId, true)
        val fakeTyping2 = Typing("userId2", channelId, true)

        coEvery { typingIndicator.setTyping(any(), any(), any(), any()) } just Runs

        service.bind(channelId)
        runBlocking {
            service.setTyping(
                fakeTyping.userId,
                fakeTyping.channelId,
                fakeTyping.isTyping,
                fakeTyping.timestamp
            )
            service.setTyping(
                fakeTyping2.userId,
                fakeTyping2.channelId,
                fakeTyping2.isTyping,
                fakeTyping2.timestamp
            )
        }

        val map2: TypingMap =
            hashMapOf(fakeTyping.userId to fakeTyping, fakeTyping2.userId to fakeTyping2)
        verify {
            service["emitNewData"](eq(map2))
        }
    }

    @Test
    fun whenSetTypingDataIsCalled_andUserIsOnList_thenEmitNewDataIsExecuted_andPreviousUserDataIsReplaced() {
        val channelId: ChannelId = "fakeChannel"
        val fakeTyping = Typing("userId", channelId, false)
        val fakeTyping2 = Typing("userId", channelId, true)

        coEvery { typingIndicator.setTyping(any(), any(), any(), any()) } just Runs

        service.bind(channelId)
        runBlocking {
            service.setTyping(
                fakeTyping.userId,
                fakeTyping.channelId,
                fakeTyping.isTyping,
                fakeTyping.timestamp
            )
            service.setTyping(
                fakeTyping2.userId,
                fakeTyping2.channelId,
                fakeTyping2.isTyping,
                fakeTyping2.timestamp
            )
        }

        val map2: TypingMap = hashMapOf(fakeTyping2.userId to fakeTyping2)
        verify {
            service["emitNewData"](eq(map2))
        }
    }

    @Test
    fun whenSetTypingIsCalled_andShouldSendTypingEventReturnsTrue_thenTypingIndicatorSetTypingIsCalled() {
        val channelId: ChannelId = "fakeChannel"
        val fakeTyping = Typing("userId", channelId, false)

        coEvery { typingIndicator.setTyping(any(), any(), any(), any()) } just Runs
        every { service["shouldSendTypingEvent"](eq(fakeTyping)) } returns true
        service.bind(channelId)
        runBlocking {
            service.setTyping(
                fakeTyping.userId,
                fakeTyping.channelId,
                fakeTyping.isTyping,
                fakeTyping.timestamp
            )
        }

        coVerify {
            typingIndicator.setTyping(fakeTyping.channelId, fakeTyping.isTyping, any(), any())
        }
    }

    @Test
    fun whenSetTypingIsCalled_andShouldSendTypingEventReturnsFalse_thenTypingIndicatorSetTypingIsNotCalled() {
        val channelId: ChannelId = "fakeChannel"
        val fakeTyping = Typing("userId", channelId, false)

        coEvery { typingIndicator.setTyping(any(), any(), any(), any()) } just Runs
        every { service["shouldSendTypingEvent"](eq(fakeTyping)) } returns false
        service.bind(channelId)
        runBlocking {
            service.setTyping(
                fakeTyping.userId,
                fakeTyping.channelId,
                fakeTyping.isTyping,
                fakeTyping.timestamp
            )
        }

        coVerify(exactly = 0) {
            typingIndicator.setTyping(fakeTyping.channelId, fakeTyping.isTyping, any(), any())
        }
    }


}
