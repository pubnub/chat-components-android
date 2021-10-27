package com.pubnub.components

import androidx.paging.*
import com.pubnub.components.chat.network.paging.MessageRemoteMediator
import com.pubnub.components.chat.service.message.MessageService
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.repository.message.MessageRepository
import com.pubnub.framework.data.ChannelId
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.*
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
class MessageRemoteMediatorUnitTest {

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

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @MockK(relaxed = true)
    lateinit var service: MessageService<DBMessage>

    @MockK(relaxed = true)
    lateinit var messageRepository: MessageRepository<DBMessage, DBMessage>

    private val messageCount: Int = 10
    private val channelId: ChannelId = "channel.lobby"
    private val messageRemoteMediator: MessageRemoteMediator by lazy {
        MessageRemoteMediator(
            channelId,
            service,
            messageRepository,
            messageCount,
            testCoroutineScope,
        )
    }

    @Before
    fun setup() {
        MockKAnnotations.init(
            this,
            relaxed = true,
            relaxUnitFun = true,
            overrideRecordPrivateCalls = true
        )
    }

    @Before
    fun tearDown() {
        clearAllMocks()
    }

    // region Mediator load
    @Test
    fun whenRefreshIsReceived_thenTimeWindowForInitIsCalled() = runBlocking {
        val state: PagingState<Int, DBMessage> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
        mediator.load(LoadType.REFRESH, state)

        verify(exactly = 1) { mediator["getTimeWindowForInitMessage"]() }
        verify(exactly = 0) { mediator["getTimeWindowForFirstMessage"](state) }
        verify(exactly = 0) { mediator["getTimeWindowForLastMessage"](state) }
    }

    @Test
    fun whenAppendIsReceived_thenTimeWindowForFirstMessageIsCalled() = runBlocking {
        val state: PagingState<Int, DBMessage> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
        mediator.load(LoadType.APPEND, state)

        verify(exactly = 0) { mediator["getTimeWindowForInitMessage"]() }
        verify(exactly = 1) { mediator["getTimeWindowForFirstMessage"](state) }
        verify(exactly = 0) { mediator["getTimeWindowForLastMessage"](state) }
    }

    @Test
    fun whenPrependIsReceived_thenTimeWindowForLastMessageIsCalled() = runBlocking {
        val state: PagingState<Int, DBMessage> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
        mediator.load(LoadType.PREPEND, state)

        verify(exactly = 0) { mediator["getTimeWindowForInitMessage"]() }
        verify(exactly = 0) { mediator["getTimeWindowForFirstMessage"](state) }
        verify(exactly = 1) { mediator["getTimeWindowForLastMessage"](state) }
    }
    // endregion

    // region Time window
    @ExperimentalPagingApi
    @Test
    fun whenPageIsNull_thenResultSuccessIsReturned() = runBlocking {
        val state: PagingState<Int, DBMessage> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val result = mediator.load(LoadType.REFRESH, state)

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun whenPageIsNotNull_andRefreshIsReceived_thenFullTimeWindowIsReturned() {
        coEvery { messageRepository.getLast(channelId) } returns null

        val state: PagingState<Int, DBMessage> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
        val time = System.currentTimeMillis() * 10_000L
        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(LoadType.REFRESH, state) }

        Assert.assertNull(pageEnd.lastOrNull())
        Assert.assertTrue(pageStart.last()!! in time..(System.currentTimeMillis() * 10_000L))
    }

    @ExperimentalPagingApi
    @Test
    fun whenAppendIsReceived_andHasMoreMessagesInDb_thenNullIsReturned() {
        coEvery { messageRepository.getLast(channelId) } returns null
        coEvery { messageRepository.hasMoreBefore(any(), any()) } returns true

        val message: DBMessage = mockk(relaxed = true) {
            every { timetoken } returns 1L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessage>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessage> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val result = runBlocking { mediator.load(LoadType.APPEND, state) }

        coVerify(exactly = 1) { messageRepository.hasMoreBefore(eq(channelId), eq(1L)) }

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @ExperimentalPagingApi
    @Test
    fun whenAppendIsReceived_andHasNoMoreMessagesInDb_thenPageIsReturned() {

        val message: DBMessage = mockk(relaxed = true) {
            every { timetoken } returns 1L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessage>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessage> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()

        coEvery { messageRepository.getLast(channelId) } returns null
        coEvery { messageRepository.hasMoreBefore(any(), any()) } returns false
        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(LoadType.APPEND, state) }

        coVerify(exactly = 1) { messageRepository.hasMoreBefore(eq(channelId), eq(1L)) }

        Assert.assertEquals(pageStart.last(), 1L)
        Assert.assertNull(pageEnd.lastOrNull())
    }

    @Test
    fun whenPageIsNotNull_andAppendIsReceived_thenFullTimeWindowIsReturned() = runBlocking {
        coEvery { messageRepository.hasMoreBefore(any(), any()) } returns false

        val message: DBMessage = mockk(relaxed = true) {
            every { timetoken } returns 1L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessage>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessage> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        mediator.load(LoadType.APPEND, state)

        Assert.assertEquals(pageStart.last(), 1L)
        Assert.assertNull(pageEnd.lastOrNull())
    }

    @ExperimentalPagingApi
    @Test
    fun whenPrependIsReceived_andHasMoreMessagesInDb_thenNullIsReturned() {
        coEvery { messageRepository.hasMoreAfter(any(), any()) } returns true

        val message: DBMessage = mockk(relaxed = true) {
            every { timetoken } returns 1L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessage>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessage> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val result = runBlocking { mediator.load(LoadType.PREPEND, state) }

        coVerify(exactly = 1) { messageRepository.hasMoreAfter(eq(channelId), eq(1L)) }

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @ExperimentalPagingApi
    @Test
    fun whenPrependIsReceived_andHasNoMoreMessagesInDb_thenPageIsReturned() {

        coEvery { messageRepository.hasMoreAfter(any(), any()) } returns false
        val message: DBMessage = mockk(relaxed = true) {
            every { timetoken } returns 1L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessage>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessage> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(LoadType.PREPEND, state) }

        coVerify(exactly = 1) { messageRepository.hasMoreAfter(eq(channelId), eq(1L)) }

        Assert.assertNull(pageStart.lastOrNull())
        Assert.assertEquals(pageEnd.lastOrNull(), 1 + 1L)
    }
    // endregion

}