package com.pubnub.components

import android.content.Context
import androidx.annotation.CallSuper
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.components.data.Database
import com.pubnub.components.data.member.DBMember
import com.pubnub.framework.data.UserId

import io.mockk.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Before

@OptIn(DelicateCoroutinesApi::class)
open class BaseTest {

    protected var pubNub: PubNub? = null

    private val userId: UserId = "fakeUser"

    @Before
    @CallSuper
    open fun setUp() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)
    }

    @Before
    @CallSuper
    open fun tearDown() {
        clearAllMocks()
    }

    fun mockPubNub() {

        val configuration: PNConfiguration = mockk(relaxed = true, relaxUnitFun = true)
        every { configuration.uuid } returns userId

        pubNub = mockk(relaxed = true, relaxUnitFun = true)
        every { pubNub!! getProperty "configuration" } returns configuration
    }

    fun unmockPubNub() {
        pubNub = null
    }

    fun mockDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mockkObject(Database)

        every { Database.INSTANCE } returns Room.inMemoryDatabaseBuilder(
            context,
            DefaultDatabase::class.java
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                GlobalScope.launch(Dispatchers.IO) {
                    with(Database.INSTANCE) {
                        // add test member
                        memberDao().insert(
                            DBMember(
                                userId,
                                "test",
                                null,
                                null,
                                null,
                                DBMember.CustomData("asd"),
                                null,
                                null
                            ),
                        )
                    }
                }
            }
        })
            .fallbackToDestructiveMigration()
            .build().asPubNub()
    }

    fun unmockDatabase() {
        unmockkObject(Database)
    }

    private fun resetField(target: Any, fieldName: String) {
        val field = target.javaClass.getDeclaredField(fieldName)

        with(field) {
            isAccessible = true
            set(target, null)
        }
    }
}