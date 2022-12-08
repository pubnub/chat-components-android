package com.pubnub.components.chat.provider

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.components.data.sync.DBRemoteTimetoken
import com.pubnub.components.repository.sync.RemoteTimetokenRepository

val LocalRemoteTimetokenRepository =
    compositionLocalOf<RemoteTimetokenRepository<DBRemoteTimetoken>> { throw RemoteTimetokenRepositoryNotInitializedException() }

class RemoteTimetokenRepositoryNotInitializedException :
    Exception("Remote Timetoken repository not initialized")
