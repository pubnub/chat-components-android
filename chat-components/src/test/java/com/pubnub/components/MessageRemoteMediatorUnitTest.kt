package com.pubnub.components

import androidx.paging.*
import com.pubnub.components.chat.network.data.NetworkMessagePayload
import com.pubnub.components.chat.network.paging.MessageRemoteMediator
import com.pubnub.components.chat.network.paging.MessageRemoteMediator.Companion.TABLE_NAME
import com.pubnub.components.chat.service.error.NoLogger
import com.pubnub.components.chat.service.message.MessageService
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.data.sync.DBRemoteTimetoken
import com.pubnub.components.repository.sync.RemoteTimetokenRepository
import com.pubnub.framework.data.ChannelId
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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

    private val testCoroutineDispatcher = StandardTestDispatcher()

    private val testCoroutineScope = TestScope(testCoroutineDispatcher)

    @MockK(relaxed = true)
    lateinit var service: MessageService<NetworkMessagePayload>

    @MockK(relaxed = true)
    lateinit var remoteTimetokenRepository: RemoteTimetokenRepository<DBRemoteTimetoken>

    private val messageCount: Int = 10
    private val channelId: ChannelId = "channel.lobby"
    private val messageRemoteMediator: MessageRemoteMediator by lazy {
        MessageRemoteMediator(
            service,
            remoteTimetokenRepository,
            NoLogger(),
            messageCount,
            testCoroutineScope,
        ).apply {
            setChannel(channelId)
        }
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
        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns null

        val state: PagingState<Int, DBMessageWithActions> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)


        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.REFRESH

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd),
            )
        } answers {}

        mediator.load(loadType, state)

        verify(exactly = 1) { mediator["getTimeWindowForInitMessage"]() }
        verify(exactly = 0) { mediator["getTimeWindowForFirstMessage"](state) }
        verify(exactly = 0) { mediator["getTimeWindowForLastMessage"](state) }
    }

    @Test
    fun whenAppendIsReceived_thenTimeWindowForFirstMessageIsCalled() = runBlocking {
        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns null

        val state: PagingState<Int, DBMessageWithActions> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
        mediator.load(LoadType.APPEND, state)

        verify(exactly = 0) { mediator["getTimeWindowForInitMessage"]() }
        verify(exactly = 1) { mediator["getTimeWindowForFirstMessage"](state) }
        verify(exactly = 0) { mediator["getTimeWindowForLastMessage"](state) }
    }

    @Test
    fun whenPrependIsReceived_thenTimeWindowForLastMessageIsCalled() = runBlocking {
        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns null

        val state: PagingState<Int, DBMessageWithActions> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
        mediator.load(LoadType.PREPEND, state)

        verify(exactly = 0) { mediator["getTimeWindowForInitMessage"]() }
        verify(exactly = 0) { mediator["getTimeWindowForFirstMessage"](state) }
        verify(exactly = 1) { mediator["getTimeWindowForLastMessage"](state) }
    }
    // endregion

    // region Time window

    @Test
    fun whenPageIsNull_andPrependIsReceived_thenResultSuccessIsReturned() = runBlocking {
        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns null
        val state: PagingState<Int, DBMessageWithActions> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val result = mediator.load(LoadType.PREPEND, state)

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun whenPageIsNotNull_andRefreshIsReceived_andHasNoRemoteTimetoken_thenFullTimeWindowIsReturned() {
        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns null

        val state: PagingState<Int, DBMessageWithActions> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
        val time = System.currentTimeMillis() * 10_000L
        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.REFRESH

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        Assert.assertNull(pageEnd.lastOrNull())
        Assert.assertTrue(pageStart.last()!! in time..(System.currentTimeMillis() * 10_000L))
    }

    @Test
    fun whenPageIsNotNull_andRefreshIsReceived_andHasRemoteTimetoken_thenFullTimeWindowIsReturned() {
        val start = 99L
        val end = 199L

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns DBRemoteTimetoken(
            TABLE_NAME,
            channelId,
            start,
            end
        )

        val state: PagingState<Int, DBMessageWithActions> = mockk(relaxed = true)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.REFRESH

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

//        Assert.assertTrue(pageStart.last()!! in time..(System.currentTimeMillis() * 10_000L))
        Assert.assertNull(pageStart.lastOrNull())
        Assert.assertEquals(pageEnd.last()!!, end)
    }


    @Test
    fun whenAppendIsReceived_andHasRemoteTimetoken_thenPageIsReturned() = runBlocking {
        val start = 99L
        val end = 199L

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns DBRemoteTimetoken(
            TABLE_NAME,
            channelId,
            start,
            end
        )

        val message: DBMessageWithActions = mockk(relaxed = true) {
            every { message.timetoken } returns 1L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.APPEND

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        val result = mediator.load(loadType, state)

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }

        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
        Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)


        Assert.assertEquals(start, pageStart.lastOrNull())
        Assert.assertNull(pageEnd.lastOrNull())
    }


    @Test
    fun whenAppendIsReceived_andHasNoRemoteTimetoken_thenNullIsReturned() {

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns null

        val message: DBMessageWithActions = mockk(relaxed = true) {
            every { message.timetoken } returns 1L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.APPEND

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }

        Assert.assertNull(pageStart.lastOrNull())
        Assert.assertNull(pageEnd.lastOrNull())
    }

//    @Test
//    fun whenPageIsNotNull_andAppendIsReceived_thenFullTimeWindowIsReturned() = runBlocking {
//        val start = 99L
//        val end = 199L
//
//        coEvery { remoteTimetokenRepository.getAll(TABLE_NAME, channelId) } returns flowOf(listOf(
//            DBRemoteTimetoken(TABLE_NAME, channelId, start, end)
//        ))
//
//        val message: DBMessageWithActions = mockk(relaxed = true) {
//            every { message.timetoken } returns 1L
//        }
//
//        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
//            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
//        val state: PagingState<Int, DBMessageWithActions> =
//            PagingState(data, null, PagingConfig(10), 0)
//        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
//
//        val pageStart = mutableListOf<Long?>()
//        val pageEnd = mutableListOf<Long?>()
//        coEvery {
//            mediator["loadNewMessages"](
//                eq(channelId),
//                captureNullable(pageStart),
//                captureNullable(pageEnd)
//            )
//        } answers {}
//
//        mediator.load(LoadType.APPEND, state)
//
//        Assert.assertEquals(pageStart.last(), 1L)
//        Assert.assertNull(pageEnd.lastOrNull())
//    }
//
//    
//    @Test
//    fun whenPrependIsReceived_andHasMoreMessagesInDb_thenNullIsReturned() {
//        coEvery { messageRepository.hasMoreAfter(any(), any()) } returns true
//
//        val message: DBMessageWithActions = mockk(relaxed = true) {
//            every { message.timetoken } returns 1L
//        }
//
//        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
//            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
//        val state: PagingState<Int, DBMessageWithActions> =
//            PagingState(data, null, PagingConfig(10), 0)
//        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
//
//        val result = runBlocking { mediator.load(LoadType.PREPEND, state) }
//
//        coVerify(exactly = 1) { messageRepository.hasMoreAfter(eq(channelId), eq(1L)) }
//
//        Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
//        Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
//    }
//
//    
//    @Test
//    fun whenPrependIsReceived_andHasNoMoreMessagesInDb_thenPageIsReturned() {
//
//        coEvery { messageRepository.hasMoreAfter(any(), any()) } returns false
//        val message: DBMessageWithActions = mockk(relaxed = true) {
//            every { message.timetoken } returns 1L
//        }
//
//        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
//            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
//        val state: PagingState<Int, DBMessageWithActions> =
//            PagingState(data, null, PagingConfig(10), 0)
//        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
//        val pageStart = mutableListOf<Long?>()
//        val pageEnd = mutableListOf<Long?>()
//
//        coEvery {
//            mediator["loadNewMessages"](
//                eq(channelId),
//                captureNullable(pageStart),
//                captureNullable(pageEnd)
//            )
//        } answers {}
//
//        runBlocking { mediator.load(LoadType.PREPEND, state) }
//
//        coVerify(exactly = 1) { messageRepository.hasMoreAfter(eq(channelId), eq(1L)) }
//
//        Assert.assertNull(pageStart.lastOrNull())
//        Assert.assertEquals(pageEnd.lastOrNull(), 1 + 1L)
//    }
//    // endregion


    // region Refresh

    @Test
    fun whenRefreshIsReceived_andHasNoRemoteTimetoken_thenNetworkIsCalledWithCurrentStartAndNullEnd() {

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns null

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)
        val time = System.currentTimeMillis() * 10_000L

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.REFRESH

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }

        Assert.assertTrue(pageStart.last()!! in time..(System.currentTimeMillis() * 10_000L))
        Assert.assertNull(pageEnd.lastOrNull())

    }

    @Test
    fun whenRefreshIsReceived_andHasRemoteTimetoken_thenNetworkIsCalledWithCurrentStartAndLastKnownEnd() {

        val min = 100L
        val max = 200L

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns
                DBRemoteTimetoken(TABLE_NAME, channelId, min, max)

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.REFRESH

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }

//        Assert.assertTrue(pageStart.last()!! in time..(System.currentTimeMillis() * 10_000L))
        Assert.assertNull(pageStart.lastOrNull())
        Assert.assertEquals(max, pageEnd.lastOrNull())

    }
    // endregion

    // region Prepend
    @Test
    fun whenPrependIsReceived_andHasNoRemoteTimetoken_thenNetworkIsNotCalled() {

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns null

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        runBlocking {
            val result = mediator.load(LoadType.PREPEND, state)

            Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
            Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }
    }

    @Test
    fun whenPrependIsReceived_andHasRemoteTimetoken_andLastMessageTimeTokenIsLowerThanStart_thenNetworkIsCalledWithNullStartAndLastKnownEndPlusOne() {

        val min = 100L
        val max = 200L

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns
                DBRemoteTimetoken(TABLE_NAME, channelId, min, max)

        val message: DBMessageWithActions = mockk(relaxed = true) {
            every { message.timetoken } returns 1L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.PREPEND
        val time = System.currentTimeMillis() * 10_000L


        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }

//        Assert.assertNull(pageStart.lastOrNull())
        Assert.assertTrue(pageStart.last()!! in time..(System.currentTimeMillis() * 10_000L))
        Assert.assertEquals(max + 1, pageEnd.last())

    }

    @Test
    fun whenPrependIsReceived_andHasRemoteTimetoken_andLastMessageTimeTokenIsBetweenStartAndEnd_thenNetworkIsCalledWithNullStartAndLastKnownEndPlusOne() {

        val min = 100L
        val max = 200L

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns
                DBRemoteTimetoken(TABLE_NAME, channelId, min, max)

        val message: DBMessageWithActions = mockk(relaxed = true) {
            every { message.timetoken } returns 50L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.PREPEND
        val time = System.currentTimeMillis() * 10_000L

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }

//        Assert.assertNull(pageStart.lastOrNull())
        Assert.assertTrue(pageStart.last()!! in time..(System.currentTimeMillis() * 10_000L))
        Assert.assertEquals(max + 1, pageEnd.last())

    }

    @Test
    fun whenPrependIsReceived_andHasRemoteTimetoken_andLastMessageTimeTokenIsHigherThanEnd_thenNetworkIsCalledWithNullStartAndLastKnownEndPlusOne() {

        val min = 100L
        val max = 200L

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns
                DBRemoteTimetoken(TABLE_NAME, channelId, min, max)

        val message: DBMessageWithActions = mockk(relaxed = true) {
            every { message.timetoken } returns 300L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.PREPEND
        val time = System.currentTimeMillis() * 10_000L

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }


//        Assert.assertNull(pageStart.lastOrNull())
        Assert.assertTrue(pageStart.last()!! in time..(System.currentTimeMillis() * 10_000L))
        Assert.assertEquals(max + 1, pageEnd.last())

    }
    // endregion

    // region Append
    @Test
    fun whenAppendIsReceived_andHasNoRemoteTimetoken_thenNetworkIsNotCalled() {

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns null

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        runBlocking {
            val result = mediator.load(LoadType.APPEND, state)

            Assert.assertTrue(result is RemoteMediator.MediatorResult.Success)
            Assert.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }

//        coVerify(exactly = 0) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }
    }

    @Test
    fun whenAppendIsReceived_andHasRemoteTimetoken_andLastMessageTimeTokenIsLowerThanStart_thenNetworkIsCalledWithLastKnownStartAndNullEnd() {

        val min = 100L
        val max = 200L

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns
                DBRemoteTimetoken(TABLE_NAME, channelId, min, max)

        val message: DBMessageWithActions = mockk(relaxed = true) {
            every { message.timetoken } returns 1L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.APPEND

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }

        Assert.assertEquals(min, pageStart.last())
        Assert.assertNull(pageEnd.lastOrNull())

    }

    @Test
    fun whenAppendIsReceived_andHasRemoteTimetoken_andLastMessageTimeTokenIsBetweenStartAndEnd_thenNetworkIsCalledWithLastKnownStartAndNullEnd() {

        val min = 100L
        val max = 200L

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns
                DBRemoteTimetoken(TABLE_NAME, channelId, min, max)

        val message: DBMessageWithActions = mockk(relaxed = true) {
            every { message.timetoken } returns 50L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.APPEND

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }

        Assert.assertEquals(min, pageStart.last())
        Assert.assertNull(pageEnd.lastOrNull())
    }

    @Test
    fun whenAppendIsReceived_andHasRemoteTimetoken_andLastMessageTimeTokenIsHigherThanEnd_thenNetworkIsCalledWithLastKnownStartAndNullEnd() {

        val min = 100L
        val max = 200L

        coEvery { remoteTimetokenRepository.get(TABLE_NAME, channelId) } returns
                DBRemoteTimetoken(TABLE_NAME, channelId, min, max)

        val message: DBMessageWithActions = mockk(relaxed = true) {
            every { message.timetoken } returns 300L
        }

        val data: List<PagingSource.LoadResult.Page<Int, DBMessageWithActions>> =
            listOf(PagingSource.LoadResult.Page(listOf(message), null, null))
        val state: PagingState<Int, DBMessageWithActions> =
            PagingState(data, null, PagingConfig(10), 0)
        val mediator: MessageRemoteMediator = spyk(messageRemoteMediator, recordPrivateCalls = true)

        val pageStart = mutableListOf<Long?>()
        val pageEnd = mutableListOf<Long?>()
        val loadType = LoadType.APPEND

        coEvery {
            mediator["loadNewMessages"](
                eq(channelId),
                eq(loadType),
                captureNullable(pageStart),
                captureNullable(pageEnd)
            )
        } answers {}

        runBlocking { mediator.load(loadType, state) }

        coVerify(exactly = 1) { remoteTimetokenRepository.get(eq(TABLE_NAME), eq(channelId)) }

        Assert.assertEquals(min, pageStart.last())
        Assert.assertNull(pageEnd.lastOrNull())

    }
    // endregion
}
