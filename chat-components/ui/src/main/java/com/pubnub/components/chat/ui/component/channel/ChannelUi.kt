package com.pubnub.components.chat.ui.component.channel

import androidx.annotation.StringDef
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.framework.data.ChannelId
import kotlinx.datetime.Instant

interface ChannelUi {
    data class Data(
        val id: ChannelId,
        val name: String,
        @TypeDef val type: String,
        val members: List<MemberUi.Data>,
        val description: String? = null,
        val profileUrl: String? = null,
        val updated: Instant? = null,
        val status: String? = null,
        val custom: Any? = null,
    ) : ChannelUi {
        @Retention(AnnotationRetention.SOURCE)
        @StringDef(DEFAULT, DIRECT, GROUP)
        annotation class TypeDef
        companion object {
            const val DEFAULT = "default"
            const val DIRECT = "direct"
            const val GROUP = "group"

            @TypeDef
            fun typeFromString(value: String?): String =
                when (value) {
                    GROUP -> GROUP
                    DIRECT -> DIRECT
                    else -> DEFAULT
                }
        }
    }

    data class Header(val title: String) : ChannelUi
}
