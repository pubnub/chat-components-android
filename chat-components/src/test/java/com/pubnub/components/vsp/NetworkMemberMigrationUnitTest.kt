package com.pubnub.components.vsp

import com.pubnub.api.managers.MapperManager
import com.pubnub.components.TestTree
import com.pubnub.components.chat.network.data.NetworkChannelMetadata
import com.pubnub.components.chat.network.data.NetworkMember
import com.pubnub.components.chat.network.data.type
import com.pubnub.components.chat.network.mapper.NetworkMemberMapper
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkMemberMigrationUnitTest {
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

    private val networkMemberMapper = NetworkMemberMapper(MapperManager())

    @Before
    fun setup() {
        MockKAnnotations.init(
            this,
            relaxed = true,
            relaxUnitFun = true,
            overrideRecordPrivateCalls = true
        )
        mockkStatic(NetworkMember::type)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkObject(NetworkChannelMetadata::type)
    }

    @Test
    fun whenTypeExists_thenTypeIsSet() = runTest {
        val expectedType = "myType"
        val data: NetworkMember = mockk(relaxed = true)
        every { data.type } returns expectedType
        val result = networkMemberMapper.map(data)

        Assert.assertEquals(expectedType, result.type)
    }

    @Test
    fun whenTypeDoesntExistsInCustom_thenDefaultValueIsSet() = runTest {
        val expectedType = "default"
        val data: NetworkMember = mockk(relaxed = true)
        every { data.type } returns null
        val result = networkMemberMapper.map(data)

        Assert.assertEquals(expectedType, result.type)
    }
}
