package com.pubnub.components.chat.data

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pubnub.components.DefaultDatabase
import com.pubnub.components.chat.network.mapper.NetworkChannelMapper
import com.pubnub.components.data.Database
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(DelicateCoroutinesApi::class)
fun RoomDatabase.Builder<DefaultDatabase>.prepopulate(
    context: Context,
    onReady: () -> Unit = {}
): RoomDatabase.Builder<DefaultDatabase> =
    addCallback(
        object : RoomDatabase.Callback() {
            var onCreateCalled: Boolean = false

            init {
                Timber.e("Add callback")
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                Timber.e("on open")
                // Call only if onCreate was not called
                if (!onCreateCalled)
                    onReady()
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Timber.e("on create")
                onCreateCalled = true

                val defaultDataRepository = DefaultDataRepository(context.resources)

                // insert the data on the IO Thread
                GlobalScope.launch(Dispatchers.IO) {
                    with(Database.INSTANCE) {

                        // add members
                        val members = defaultDataRepository.members
                        memberDao().insert(*members)

                        val channelMapper = NetworkChannelMapper()
                        val channels = defaultDataRepository.channels.map { channelMapper.map(it) }
                            .toTypedArray()

                        channelDao().insert(*channels)

//                        val messages = defaultDataRepository.messages
//                        messageDao().insert(*messages)

                        val membership = defaultDataRepository.membership
                        membershipDao().insert(*membership)

                        Timber.e("DATABASE: Created")
                        onReady()
                    }
                }
            }
        }
    )