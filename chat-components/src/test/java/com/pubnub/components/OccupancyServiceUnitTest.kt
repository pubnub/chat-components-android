package com.pubnub.components

import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.components.chat.network.mapper.NetworkOccupancyMapper
import com.pubnub.components.chat.service.channel.DefaultOccupancyService
import com.pubnub.components.chat.service.error.NoLogger
import com.pubnub.framework.data.OccupancyMap
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.*
import timber.log.Timber
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OccupancyServiceUnitTest {
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

    lateinit var service: DefaultOccupancyService

    private val testCoroutineDispatcher = StandardTestDispatcher()

    private val testCoroutineScope = TestScope(testCoroutineDispatcher)

    @Before
    fun setup() {
        MockKAnnotations.init(
            this,
            relaxed = true,
            relaxUnitFun = true,
            overrideRecordPrivateCalls = true
        )
        service = spyk(
            DefaultOccupancyService(
                mockk(relaxUnitFun = true, relaxed = true),
                "userId",
                NetworkOccupancyMapper(),
                NoLogger(),
                testCoroutineScope,
            ), recordPrivateCalls = true
        )
    }


    @After
    fun tearDown() {
        clearAllMocks()
    }

    // region Binding
    @Test
    fun whenBindIsCalled_thenHereNowIsExecuted() {
        every { service["listenForPresence"]() } answers {}
        coEvery { service["callHereNow"]() } answers {}

        service.bind()

        verify(exactly = 1, timeout = 10_000L) { service["callHereNow"]() }
    }

    @Test
    fun whenBindIsCalled_thenListenForPresenceIsExecuted() {
        every { service["listenForPresence"]() } answers {}

        service.bind()

        verify(exactly = 1) { service["listenForPresence"]() }
    }
    // endregion

    // region Occupancy
    @Test
    fun whenCallHereNowIsCalled_thenGetOccupancyIsExecuted() {
        every { service["listenForPresence"]() } answers {}

        service.bind()

        coVerifySequence {
            service.bind()
            service["listenForPresence"]()
            service["callHereNow"]()
            service["getOccupancy"]()
        }
    }

    @Test
    fun whenGetOccupancyIsCalled_thenSetOccupancyIsExecuted() {
        val occupancy: OccupancyMap = mockk(relaxed = true, relaxUnitFun = true)
        every { service["listenForPresence"]() } answers {}
        every { service["getOccupancy"]() } returns occupancy

        service.bind()

        coVerifySequence {
            service.bind()
            service["listenForPresence"]()
            service["callHereNow"]()
            service["getOccupancy"]()
            service["setOccupancy"](eq(occupancy))
        }
    }
    // endregion

    // region Process Action
    @Test
    fun whenPresenceIsReceived_thenProcessActionIsExecuted() {
        val event = PNPresenceEventResult()

        coEvery { service["callHereNow"]() } answers {}

        service.bind()

        service.callPrivateSuspend("processAction", event)
        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(event)) }
    }

    @Test
    fun whenPNPresenceEventResultIsReceived_andEventIsJoin_thenUuidIsAddedToList() {
        val event = PNPresenceEventResult(
            channel = "channel",
            event = "join",
            uuid = "test-member",
            occupancy = 1,
        )
        val occupancyMap: CapturingSlot<OccupancyMap> = CapturingSlot()


        coEvery { service["callHereNow"]() } answers {}

        service.bind()
        service.callPrivateSuspend("processAction", event)

        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(event)) }

        coVerify(exactly = 1) { service["setOccupancy"](capture(occupancyMap)) }

        with(occupancyMap.captured) {
            assertTrue { this.containsKey("channel") }
            assertEquals(1, this["channel"]!!.occupancy)
            assertTrue { this["channel"]!!.list!!.contains("test-member") }
        }
    }

    @Test
    fun whenPNPresenceEventResultIsReceived_andEventIsLeave_thenUuidIsRemovedFromList() {
        val join = PNPresenceEventResult(
            channel = "channel",
            event = "join",
            uuid = "test-member",
            occupancy = 1,
        )
        val event = PNPresenceEventResult(
            channel = "channel",
            event = "leave",
            uuid = "test-member",
            occupancy = 0,
        )
        val occupancyMap: MutableList<OccupancyMap> = mutableListOf()

        coEvery { service["callHereNow"]() } answers {}

        service.bind()

        service.callPrivateSuspend("processAction", join)
        service.callPrivateSuspend("processAction", event)

        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(join)) }
        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(event)) }

        coVerify(exactly = 2) { service["setOccupancy"](capture(occupancyMap)) }

        with(occupancyMap.last()) {
            assertTrue { this.containsKey("channel") }
            assertEquals(0, this["channel"]!!.occupancy)
            assertTrue { this["channel"]!!.list!!.isEmpty() }
        }
    }

    @Test
    fun whenPNPresenceEventResultIsReceived_andEventIsTimeout_thenUuidIsRemovedFromList() {
        val join = PNPresenceEventResult(
            channel = "channel",
            event = "join",
            uuid = "test-member",
            occupancy = 1,
        )
        val event = PNPresenceEventResult(
            channel = "channel",
            event = "timeout",
            uuid = "test-member",
            occupancy = 0,
        )
        val occupancyMap: MutableList<OccupancyMap> = mutableListOf()

        coEvery { service["callHereNow"]() } answers {}

        service.bind()

        service.callPrivateSuspend("processAction", join)
        service.callPrivateSuspend("processAction", event)

        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(join)) }
        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(event)) }

        coVerify(exactly = 2) { service["setOccupancy"](capture(occupancyMap)) }

        with(occupancyMap.last()) {
            assertTrue { this.containsKey("channel") }
            assertEquals(0, this["channel"]!!.occupancy)
            assertTrue { this["channel"]!!.list!!.isEmpty() }
        }
    }

    @Test
    fun whenPNPresenceEventResultIsReceived_andEventIsInterval_thenAllUuidsFromJoinListAreAddedToList() {
        val listOfMembers = listOf("test-member1", "test-member2", "test-member3")
        val event = PNPresenceEventResult(
            channel = "channel",
            event = "interval",
            join = listOfMembers,
            occupancy = 3,
        )
        val occupancyMap: CapturingSlot<OccupancyMap> = CapturingSlot()

        coEvery { service["callHereNow"]() } answers {}

        service.bind()
        service.callPrivateSuspend("processAction", event)

        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(event)) }

        coVerify(exactly = 1) { service["setOccupancy"](capture(occupancyMap)) }

        with(occupancyMap.captured) {
            assertTrue { this.containsKey("channel") }
            assertEquals(3, this["channel"]!!.occupancy)
            assertEquals(listOfMembers, this["channel"]!!.list)
        }
    }

    @Test
    fun whenPNPresenceEventResultIsReceived_andEventIsInterval_thenAllUuidsFromLeaveListAreRemovedFromList() {
        val listOfMembers = listOf("test-member1", "test-member2")
        val join = PNPresenceEventResult(
            channel = "channel",
            event = "interval",
            join = listOf("test-member1", "test-member2", "test-member3"),
            occupancy = 3,
        )
        val event = PNPresenceEventResult(
            channel = "channel",
            event = "interval",
            leave = listOfMembers,
            occupancy = 1,
        )
        val occupancyMap: MutableList<OccupancyMap> = mutableListOf()

        coEvery { service["callHereNow"]() } answers {}

        service.bind()

        service.callPrivateSuspend("processAction", join)
        service.callPrivateSuspend("processAction", event)

        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(join)) }
        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(event)) }

        coVerify(exactly = 2) { service["setOccupancy"](capture(occupancyMap)) }

        with(occupancyMap.last()) {
            assertTrue { this.containsKey("channel") }
            assertEquals(1, this["channel"]!!.occupancy)
            assertEquals(listOf("test-member3"), this["channel"]!!.list)
        }
    }

    @Test
    fun whenPNPresenceEventResultIsReceived_andEventIsInterval_thenAllUuidsFromTimeoutListAreRemovedFromList() {
        val listOfMembers = listOf("test-member1", "test-member2")
        val join = PNPresenceEventResult(
            channel = "channel",
            event = "interval",
            join = listOf("test-member1", "test-member2", "test-member3"),
            occupancy = 3,
        )
        val event = PNPresenceEventResult(
            channel = "channel",
            event = "interval",
            timeout = listOfMembers,
            occupancy = 1,
        )
        val occupancyMap: MutableList<OccupancyMap> = mutableListOf()

        coEvery { service["callHereNow"]() } answers {}

        service.bind()

        service.callPrivateSuspend("processAction", join)
        service.callPrivateSuspend("processAction", event)

        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(join)) }
        coVerify(exactly = 1, timeout = 10_000L) { service["processAction"](eq(event)) }

        coVerify(exactly = 2) { service["setOccupancy"](capture(occupancyMap)) }

        with(occupancyMap.last()) {
            assertTrue { this.containsKey("channel") }
            assertEquals(1, this["channel"]!!.occupancy)
            assertEquals(listOf("test-member3"), this["channel"]!!.list)
        }
    }
    // endregion

    private fun Any.callPrivateSuspend(method: String, vararg parameters: Any) = runBlocking {
        suspendCoroutineUninterceptedOrReturn<Any?> { cont ->
            val obj = this@callPrivateSuspend
            val declaredMethod = obj.javaClass.declaredMethods.first { it.name == method }
            declaredMethod.isAccessible = true
            runBlocking {
                declaredMethod.invoke(obj, *parameters, cont)
            }
        }
    }
}
