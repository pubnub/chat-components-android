package com.pubnub.components.chat.data

//import com.pubnub.components.data.membership.DBMembership
import android.content.res.Resources
import androidx.annotation.RawRes
import com.google.gson.GsonBuilder
import com.pubnub.api.models.consumer.objects.channel.PNChannelMetadata
import com.pubnub.components.chat.R
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.components.data.message.DBAttachment
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.MessageAttachmentConverter
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

internal class DefaultDataRepository(private val resources: Resources) {
    private val attachmentConverter = MessageAttachmentConverter()
    private val gson = GsonBuilder()
        .registerTypeAdapter(DBAttachment::class.java, attachmentConverter.attachmentDeserializer())
        .registerTypeAdapter(DBAttachment::class.java, attachmentConverter.attachmentSerializer())
        .enableComplexMapKeySerialization()
        .create()

    // region Default Data
    val members: Array<DBMember> = arrayOf(
        *parseArray(R.raw.users),
    )

    val channels: Array<PNChannelMetadata> = arrayOf(
        *parseArray(R.raw.channels_work),
        *parseArray(R.raw.channels_social),
        *parseArray(R.raw.channels_direct),
    )

    val messages: Array<DBMessage> = arrayOf(
        *parseArray(R.raw.messages_lorem),
        *parseArray(R.raw.messages_social),
    )

    val membership: Array<DBMembership> = arrayOf(
        *parseArray<Membership>(R.raw.membership).toDb(),
    )
    // endregion

    private inline fun <reified T> parseArray(@RawRes resource: Int): Array<out T> =
        resources.parseJson<Array<T>>(resource)

    private inline fun <reified T> Resources.parseJson(@RawRes resourceId: Int): T =
        gson.fromJson(
            openRawResource(resourceId).bufferedReader().use { it.readText() },
            T::class.java
        )

    data class Membership(
        val channel: ChannelId,
        val members: Array<UserId>,
    ) {
        fun toDb(): Array<DBMembership> =
            members.map { member -> DBMembership(channelId = channel, memberId = member) }
                .toTypedArray()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Membership

            if (channel != other.channel) return false
            if (!members.contentEquals(other.members)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = channel.hashCode()
            result = 31 * result + members.contentHashCode()
            return result
        }
    }

    private fun <T : Membership> Array<T>.toDb(): Array<DBMembership> =
        flatMap { it.toDb().toList() }.toTypedArray()

}
