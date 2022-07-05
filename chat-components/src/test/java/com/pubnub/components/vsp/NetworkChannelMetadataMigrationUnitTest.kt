package com.pubnub.components.vsp

import com.google.gson.JsonObject
import com.pubnub.components.TestTree
import com.pubnub.components.chat.network.data.NetworkChannelMetadata
import com.pubnub.components.chat.network.mapper.NetworkChannelMapper
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkChannelMetadataMigrationUnitTest {
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

    private val networkChannelMapper = NetworkChannelMapper()

    @Before
    fun setup() {
        MockKAnnotations.init(
            this,
            relaxed = true,
            relaxUnitFun = true,
            overrideRecordPrivateCalls = true
        )
        mockkStatic(NetworkChannelMetadata::type)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkObject(NetworkChannelMetadata::type)
    }

//    NetworkChannelMapper
//    not valid val avatarURL = (custom["avatarURL"] as String)
//    valid val type = input.type ?: ((custom["type"] as? String?) ?: "default")
//    valid val profileUrl = (custom["profileUrl"] as String)

    @Test
    fun whenProfileUrlExistsInCustom_thenProfileUrlIsSet() = runTest {
        val expectedProfileUrl = "myProfileUrl"
        val data: NetworkChannelMetadata = mockk(relaxed = true)
        every { data.custom } returns JsonObject().apply {
            addProperty("profileUrl", expectedProfileUrl)
        }

        val result = networkChannelMapper.map(data)

        Assert.assertEquals(expectedProfileUrl, result.profileUrl)
    }

    @Test(expected = Exception::class)
    fun whenProfileUrlDoesntExistsInCustom_thenExceptionIsThrown() = runTest {
        val data: NetworkChannelMetadata = mockk(relaxed = true)

        networkChannelMapper.map(data)
    }

    @Test
    fun whenTypeExists_thenTypeIsSet() = runTest {
        val expectedType = "myType"
        val data: NetworkChannelMetadata = mockk(relaxed = true)
        every { data.type } returns expectedType
        every { data.custom } returns JsonObject().apply {
            addProperty("profileUrl", "myProfileUrl")
        }
        val result = networkChannelMapper.map(data)

        Assert.assertEquals(expectedType, result.type)

    }

    @Test
    fun whenTypeDoesntExists_thenTypeFromCustomIsSet() = runTest {
        val expectedType = "myType"
        val data: NetworkChannelMetadata = mockk(relaxed = true)
        every { data.type } returns null
        every { data.custom } returns JsonObject().apply {
            addProperty("type", expectedType)
            addProperty("profileUrl", "myProfileUrl")
        }
        val result = networkChannelMapper.map(data)

        Assert.assertEquals(expectedType, result.type)
    }

    @Test
    fun whenTypeDoesntExistsInCustom_thenDefaultValueIsSet() = runTest {
        val expectedType = "default"
        val data: NetworkChannelMetadata = mockk(relaxed = true)
        every { data.type } returns null
        every { data.custom } returns JsonObject().apply {
            addProperty("profileUrl", "myProfileUrl")
        }
        val result = networkChannelMapper.map(data)

        Assert.assertEquals(expectedType, result.type)
    }
}
