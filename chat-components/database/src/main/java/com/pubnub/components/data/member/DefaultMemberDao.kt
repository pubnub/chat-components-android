package com.pubnub.components.data.member


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface DefaultMemberDao : MemberDao<DBMember, DBMemberWithChannels> {

    @RawQuery(observedEntities = [DBMember::class])
    override fun getAll(query: SupportSQLiteQuery): PagingSource<Int, DBMemberWithChannels>
}