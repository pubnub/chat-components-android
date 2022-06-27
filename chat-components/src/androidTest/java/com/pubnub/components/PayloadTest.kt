package com.pubnub.components

import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.components.chat.network.data.NetworkChannelMetadata
import com.pubnub.components.chat.network.data.NetworkMember
import com.pubnub.components.chat.network.data.NetworkMessage
import com.pubnub.components.chat.network.data.NetworkMessagePayload
import com.pubnub.components.chat.network.mapper.NetworkChannelMapper
import com.pubnub.components.chat.network.mapper.NetworkMemberMapper
import com.pubnub.components.chat.network.mapper.NetworkMessageMapper
import com.pubnub.components.data.channel.ChannelCustomData
import com.pubnub.components.data.member.DBMember
import com.pubnub.framework.util.asObject
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class PayloadTest {

    lateinit var pubNub: PubNub

    @Before
    fun setUp() {
        pubNub = PubNub(PNConfiguration(PubNub.generateUUID()))
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)
    }

    @After
    fun tearDown() {
        pubNub.forceDestroy()
        clearAllMocks()
        unmockkAll()
    }

    // region User

    @Test
    fun givenValidUserJson_whenIsReceived_thenNetworkMemberMapperReturnsValidDbObject() = runTest {
        // Given
        val networkMemberMapper = NetworkMemberMapper(pubNub.mapper)
        val response: NetworkMember = pubNub.mapper.fromJson(userJson, NetworkMember::class.java)
        val responseCustom = pubNub.mapper.fromJson(pubNub.mapper.toJson(response.custom), DBMember.CustomData::class.java)

        // When
        val dbObject = networkMemberMapper.map(response)

        // Then
        Assert.assertEquals(response.id, dbObject.id)
        Assert.assertEquals(response.name, dbObject.name)
        Assert.assertEquals(response.externalId, dbObject.externalId)
        Assert.assertEquals(response.profileUrl, dbObject.profileUrl)
        Assert.assertEquals(response.email, dbObject.email)
        Assert.assertEquals(responseCustom, dbObject.custom)
        Assert.assertEquals(response.updated, dbObject.updated)
        Assert.assertEquals(response.eTag, dbObject.eTag)
        Assert.assertEquals(response.type, dbObject.type)
        Assert.assertEquals(response.status, dbObject.status)
    }

    @Test
    fun givenValidUserJsonWithoutUserName_whenIsReceived_thenNetworkMemberMapperReturnsValidDbObjectWithIdAsAName() = runTest {
        // Given
        val networkMemberMapper = NetworkMemberMapper(pubNub.mapper)
        val response: NetworkMember = pubNub.mapper.fromJson(userJsonNoName, NetworkMember::class.java)
        val responseCustom = pubNub.mapper.fromJson(pubNub.mapper.toJson(response.custom), DBMember.CustomData::class.java)

        // When
        val dbObject = networkMemberMapper.map(response)

        // Then
        Assert.assertEquals(response.id, dbObject.id)
        Assert.assertEquals(response.name, dbObject.name)
        Assert.assertEquals(response.externalId, dbObject.externalId)
        Assert.assertEquals(response.profileUrl, dbObject.profileUrl)
        Assert.assertEquals(response.email, dbObject.email)
        Assert.assertEquals(responseCustom, dbObject.custom)
        Assert.assertEquals(response.updated, dbObject.updated)
        Assert.assertEquals(response.eTag, dbObject.eTag)
        Assert.assertEquals(response.type, dbObject.type)
        Assert.assertEquals(response.status, dbObject.status)
    }

    @Test
    fun givenValidUserJsonWithoutType_whenIsReceived_thenNetworkMemberMapperReturnsValidDbObjectWithDefaultType() = runTest {
        // Given
        val networkMemberMapper = NetworkMemberMapper(pubNub.mapper)
        val response: NetworkMember = pubNub.mapper.fromJson(userJsonNoType, NetworkMember::class.java)
        val responseCustom = pubNub.mapper.fromJson(pubNub.mapper.toJson(response.custom), DBMember.CustomData::class.java)

        // When
        val dbObject = networkMemberMapper.map(response)

        // Then
        Assert.assertEquals(response.id, dbObject.id)
        Assert.assertEquals(response.name, dbObject.name)
        Assert.assertEquals(response.externalId, dbObject.externalId)
        Assert.assertEquals(response.profileUrl, dbObject.profileUrl)
        Assert.assertEquals(response.email, dbObject.email)
        Assert.assertEquals(responseCustom, dbObject.custom)
        Assert.assertEquals(response.updated, dbObject.updated)
        Assert.assertEquals(response.eTag, dbObject.eTag)
        Assert.assertEquals("default", dbObject.type)
        Assert.assertEquals(response.status, dbObject.status)
    }


    @Test
    fun givenValidMinimalUserJson_whenIsReceived_thenNetworkMemberMapperReturnsValidDbObject() = runTest {
        // Given
        val networkMemberMapper = NetworkMemberMapper(pubNub.mapper)
        val response: NetworkMember = pubNub.mapper.fromJson(userJsonMin, NetworkMember::class.java)
        val responseCustom = pubNub.mapper.fromJson(pubNub.mapper.toJson(response.custom), DBMember.CustomData::class.java)

        // When
        val dbObject = networkMemberMapper.map(response)

        // Then
        Assert.assertEquals(response.id, dbObject.id)
        Assert.assertEquals(response.name, dbObject.name)
        Assert.assertEquals(response.externalId, dbObject.externalId)
        Assert.assertEquals(response.profileUrl, dbObject.profileUrl)
        Assert.assertEquals(response.email, dbObject.email)
        Assert.assertEquals(responseCustom, dbObject.custom)
        Assert.assertEquals(response.updated, dbObject.updated)
        Assert.assertEquals(response.eTag, dbObject.eTag)
        Assert.assertEquals("default", dbObject.type)
        Assert.assertEquals(response.status, dbObject.status)
    }

    // endregion

    // region Channel

    @Test
    fun givenValidChannelJson_whenIsReceived_thenNetworkChannelMapperReturnsValidDbObject() = runTest {
        // Given
        val networkMapper = NetworkChannelMapper(pubNub.mapper)
        val response: NetworkChannelMetadata = pubNub.mapper.fromJson(channelJson, NetworkChannelMetadata::class.java)
        val responseCustom: ChannelCustomData? = response.custom.asObject<ChannelCustomData?>(pubNub.mapper)?.apply {
            // profileUrl field is extracted
            remove("profileUrl")
        }

        // When
        val dbObject = networkMapper.map(response)

        // Then
        Assert.assertEquals(response.id, dbObject.id)
        Assert.assertEquals(response.name, dbObject.name)
        Assert.assertEquals(response.description, dbObject.description)
        Assert.assertEquals(responseCustom, dbObject.custom)
        Assert.assertEquals(response.updated, dbObject.updated)
        Assert.assertEquals(response.eTag, dbObject.eTag)
        Assert.assertEquals(response.type, dbObject.type)
        Assert.assertEquals(response.status, dbObject.status)
    }

    @Test
    fun givenValidMinimalChannelJson_whenIsReceived_thenNetworkChannelMapperReturnsValidDbObject() = runTest {
        // Given
        val networkMapper = NetworkChannelMapper(pubNub.mapper)
        val response: NetworkChannelMetadata = pubNub.mapper.fromJson(channelJsonMin, NetworkChannelMetadata::class.java)

        // When
        val dbObject = networkMapper.map(response)

        // Then
        Assert.assertEquals(response.id, dbObject.id)
        Assert.assertEquals(response.name, dbObject.name)
        Assert.assertEquals(response.description, dbObject.description)
        Assert.assertEquals(response.custom, dbObject.custom)
        Assert.assertEquals(response.updated, dbObject.updated)
        Assert.assertEquals(response.eTag, dbObject.eTag)
        Assert.assertEquals(response.type, dbObject.type)
        Assert.assertEquals(response.status, dbObject.status)
    }

    // endregion

    // region Message

    @Test
    fun givenValidMessageJson_whenIsReceived_thenNetworkMessageMapperReturnsValidDbObject() = runTest {
        // Given
        val networkMapper = NetworkMessageMapper(pubNub.mapper)
        val response: NetworkMessage = mockk(relaxed = true, relaxUnitFun = true)
        every { response.message } returns JsonParser.parseString(messageJson)

        val responsePayload: NetworkMessagePayload = pubNub.mapper.fromJson(pubNub.mapper.toJson(response.message), NetworkMessagePayload::class.java)

        // When
        val dbObject = networkMapper.map(response)

        // Then
        Assert.assertEquals(responsePayload.id, dbObject.id)
        Assert.assertEquals(responsePayload.text, dbObject.text)
        Assert.assertEquals(responsePayload.contentType, dbObject.contentType)
        Assert.assertEquals(responsePayload.content, dbObject.content)
        Assert.assertEquals(responsePayload.createdAt, dbObject.createdAt)
        Assert.assertEquals(responsePayload.custom, dbObject.custom)
    }

    @Test
    fun givenValidMiminalMessageJson_whenIsReceived_thenNetworkMessageMapperReturnsValidDbObject() = runTest {
        // Given
        val networkMapper = NetworkMessageMapper(pubNub.mapper)
        val response: NetworkMessage = mockk(relaxed = true, relaxUnitFun = true)
        every { response.message } returns JsonParser.parseString(messageJsonMin)

        val responsePayload: NetworkMessagePayload = pubNub.mapper.fromJson(pubNub.mapper.toJson(response.message), NetworkMessagePayload::class.java)

        // When
        val dbObject = networkMapper.map(response)

        // Then
        Assert.assertEquals(responsePayload.id, dbObject.id)
        Assert.assertEquals(responsePayload.text, dbObject.text)
        Assert.assertEquals(responsePayload.contentType, dbObject.contentType)
        Assert.assertEquals(responsePayload.content, dbObject.content)
        Assert.assertEquals(responsePayload.createdAt, dbObject.createdAt)
        Assert.assertEquals(responsePayload.custom, dbObject.custom)
    }

    // endregion

    companion object {
        const val userJson = "{\n" +
                "  \"id\": \"some-user-id\",\n" +
                "  \"name\": \"Jane Doe\",\n" +
                "  \"email\": \"jane.doe@example.com\",\n" +
                "  \"externalId\": \"some-external-user-id\",\n" +
                "  \"profileUrl\": \"https://randomuser.me/api/portraits/men/1.jpg\",\n" +
                "  \"type\": \"default\",\n" +
                "  \"status\": \"default\",\n" +
                "  \"custom\": {\n" +
                "    \"description\": \"Office Assistant\"\n" +
                "  },\n" +
                "  \"eTag\": \"AYGyoY3gre71eA\",\n" +
                "  \"updated\": \"2020-09-23T09:23:34.598494Z\"\n" +
                "}"

        const val userJsonNoName = "{\n" +
                "  \"id\": \"some-user-id\",\n" +
                "  \"email\": \"jane.doe@example.com\",\n" +
                "  \"externalId\": \"some-external-user-id\",\n" +
                "  \"profileUrl\": \"https://randomuser.me/api/portraits/men/1.jpg\",\n" +
                "  \"type\": \"default\",\n" +
                "  \"status\": \"default\",\n" +
                "  \"custom\": {\n" +
                "    \"description\": \"Office Assistant\"\n" +
                "  },\n" +
                "  \"eTag\": \"AYGyoY3gre71eA\",\n" +
                "  \"updated\": \"2020-09-23T09:23:34.598494Z\"\n" +
                "}"

        const val userJsonNoType = "{\n" +
                "  \"id\": \"some-user-id\",\n" +
                "  \"name\": \"Jane Doe\",\n" +
                "  \"email\": \"jane.doe@example.com\",\n" +
                "  \"externalId\": \"some-external-user-id\",\n" +
                "  \"profileUrl\": \"https://randomuser.me/api/portraits/men/1.jpg\",\n" +
                "  \"status\": \"default\",\n" +
                "  \"custom\": {\n" +
                "    \"description\": \"Office Assistant\"\n" +
                "  },\n" +
                "  \"eTag\": \"AYGyoY3gre71eA\",\n" +
                "  \"updated\": \"2020-09-23T09:23:34.598494Z\"\n" +
                "}"

        const val userJsonMin = "{\n" +
                "  \"id\": \"some-user-id\"\n" +
                "}"

        const val channelJson = "{\n" +
                "  \"id\": \"some-channel-id\",\n" +
                "  \"name\": \"Some Chat Channel\",\n" +
                "  \"description\": \"This is a description of the Chat Channel\",\n" +
                "  \"type\": \"default\",\n" +
                "  \"status\": \"default\",\n" +
                "  \"custom\": {\n" +
                "    \"profileUrl\": \"https://www.gravatar.com/avatar/149e60f311749f2a7c6515f7b34?s=256&d=identicon\"\n" +
                "  },\n" +
                "  \"eTag\": \"AbOx6N+6vu3zoAE\",\n" +
                "  \"updated\": \"2020-09-23T09:23:37.175764Z\"\n" +
                "}"

        const val channelJsonMin = "{\n" +
                "  \"id\": \"some-channel-id\",\n" +
                "  \"type\": \"default\"\n" +
                "}"

        const val messageJson = "{\n" +
                "  \"id\": \"6da72b98-e211-4724-aad4-e0fb9f08999f\",\n" +
                "  \"text\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit.\",\n" +
                "  \"contentType\": \"none\",\n" +
                "  \"content\": {},\n" +
                "  \"custom\": {},\n" +
                "  \"createdAt\": \"2022-05-10T14:48:00.000Z\"\n" +
                "}"

        const val messageJsonMin = "{\n" +
                "  \"id\": \"6da72b98-e211-4724-aad4-e0fb9f08999f\",\n" +
                "  \"text\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit.\",\n" +
                "  \"createdAt\": \"2022-05-10T14:48:00.000Z\"\n" +
                "}"
    }
}