package com.pubnub.components.data.channel


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface DefaultChannelDao : ChannelDao<DBChannel, DBChannelWithMembers> {

    @RawQuery(observedEntities = [DBChannel::class])
    override fun getAll(query: SupportSQLiteQuery): PagingSource<Int, DBChannelWithMembers>
}