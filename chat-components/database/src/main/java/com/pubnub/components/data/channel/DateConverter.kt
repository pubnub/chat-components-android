package com.pubnub.components.data.channel

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import java.util.*


class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun longToInstant(value: Long?): Instant? {
        return if (value == null) null else Instant.fromEpochMilliseconds(value)
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }

}
