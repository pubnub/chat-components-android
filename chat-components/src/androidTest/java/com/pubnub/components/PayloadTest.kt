package com.pubnub.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.components.chat.network.data.NetworkMember
import com.pubnub.components.chat.network.mapper.NetworkMemberMapper
import com.pubnub.components.chat.provider.ChatProvider
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.input.DefaultLocalMessageInputTheme
import com.pubnub.components.chat.ui.component.input.LocalMessageInputTheme
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class PayloadTest {

    lateinit var pubNub: PubNub

    @Before
    fun setUp() {
        pubNub = PubNub(PNConfiguration(PubNub.generateUUID()))
    }

    @After
    fun tearDown() {
        pubNub.forceDestroy()
    }

    // region User

    @Test
    fun givenValidUserJson_whenIsReceived_thenNetworkMemberMapperReturnsValidDbObject() = runTest {
        // Given
        val networkMemberMapper = NetworkMemberMapper(pubNub.mapper)
        val response: NetworkMember = pubNub.mapper.fromJson(userJson, NetworkMember::class.java)

        // When
        val dbObject = networkMemberMapper.map(response)

        // Then
        Assert.assertEquals(response.id, dbObject.id)
        Assert.assertEquals(response.name, dbObject.name)
        Assert.assertEquals(response.externalId, dbObject.externalId)
        Assert.assertEquals(response.profileUrl, dbObject.profileUrl)
        Assert.assertEquals(response.email, dbObject.email)
        Assert.assertEquals(response.custom, dbObject.custom)
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

        // When
        val dbObject = networkMemberMapper.map(response)

        // Then
        Assert.assertEquals(response.id, dbObject.id)
        Assert.assertEquals(response.id, dbObject.name)
        Assert.assertEquals(response.externalId, dbObject.externalId)
        Assert.assertEquals(response.profileUrl, dbObject.profileUrl)
        Assert.assertEquals(response.email, dbObject.email)
        Assert.assertEquals(response.custom, dbObject.custom)
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

        // When
        val dbObject = networkMemberMapper.map(response)

        // Then
        Assert.assertEquals(response.id, dbObject.id)
        Assert.assertEquals(response.name, dbObject.name)
        Assert.assertEquals(response.externalId, dbObject.externalId)
        Assert.assertEquals(response.profileUrl, dbObject.profileUrl)
        Assert.assertEquals(response.email, dbObject.email)
        Assert.assertEquals(response.custom, dbObject.custom)
        Assert.assertEquals(response.updated, dbObject.updated)
        Assert.assertEquals(response.eTag, dbObject.eTag)
        Assert.assertEquals("default", dbObject.type)
        Assert.assertEquals(response.status, dbObject.status)
    }

    // endregion

    // region Channel

    // endregion

    // region Message

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
//                "  \"custom\": {\n" +
//                "    \"description\": \"Office Assistant\",\n" +
//                "  },\n" +
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
//                "  \"custom\": {\n" +
//                "    \"description\": \"Office Assistant\",\n" +
//                "  },\n" +
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
                // TODO: how to parse it to PNUUIDMetadata ?
//                "  \"custom\": {\n" +
//                "    \"description\": \"Office Assistant\",\n" +
//                "  },\n" +
                "  \"eTag\": \"AYGyoY3gre71eA\",\n" +
                "  \"updated\": \"2020-09-23T09:23:34.598494Z\"\n" +
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

        const val messageJson = "{\n" +
                "  \"id\": \"6da72b98-e211-4724-aad4-e0fb9f08999f\",\n" +
                "  \"text\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit.\",\n" +
                "  \"contentType\": \"none\",\n" +
                "  \"content\": {},\n" +
                "  \"custom\": {},\n" +
                "  \"createdAt\": \"2022-05-10T14:48:00.000Z\"\n" +
                "}"
    }
}