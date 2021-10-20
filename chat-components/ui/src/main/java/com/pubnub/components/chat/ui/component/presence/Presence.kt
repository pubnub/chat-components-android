package com.pubnub.components.chat.ui.component.presence

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.pubnub.framework.data.UserId

class Presence {
    private val presence: HashMap<UserId, MutableState<Boolean>> = hashMapOf()

    fun get(id: UserId): State<Boolean> {
        add(id, false)
        return presence[id]!!
    }

    fun add(id: UserId, state: Boolean) {
        presence.putIfAbsent(id, mutableStateOf(state))
    }

    fun set(id: UserId, state: Boolean) {
        add(id, state)
        presence[id]!!.value = state
    }

    fun filter(function: (UserId, Boolean) -> Boolean) =
        presence.filter { (member, state) -> function(member, state.value) }
}