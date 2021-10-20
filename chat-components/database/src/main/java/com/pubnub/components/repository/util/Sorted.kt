package com.pubnub.components.repository.util


data class Sorted(val key: String = "id", val direction: Direction = Direction.ASC) {
    override fun toString(): String = "`$key` ${direction.name}"

    enum class Direction {
        ASC,
        DESC,
    }
}
