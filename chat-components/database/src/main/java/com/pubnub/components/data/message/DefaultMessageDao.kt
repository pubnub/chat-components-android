package com.pubnub.components.data.message

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface DefaultMessageDao : MessageDao<DBMessage, DBMessage> {

    @RawQuery(observedEntities = [DBMessage::class])
    override fun getAll(query: SupportSQLiteQuery): PagingSource<Int, DBMessage>
}