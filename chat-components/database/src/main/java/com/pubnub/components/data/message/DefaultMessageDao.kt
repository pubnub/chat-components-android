package com.pubnub.components.data.message

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.DBMessageWithActions

@Dao
interface DefaultMessageDao : MessageDao<DBMessage, DBMessageWithActions> {

    @RawQuery(observedEntities = [DBMessage::class, DBMessageAction::class])
    override fun getAll(query: SupportSQLiteQuery): PagingSource<Int, DBMessageWithActions>
}