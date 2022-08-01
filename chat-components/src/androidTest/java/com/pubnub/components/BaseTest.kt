package com.pubnub.components

import android.content.Context
import androidx.annotation.CallSuper
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
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

    protected val userId: UserId = "fakeUser"

    internal val context: Context
        get() = InstrumentationRegistry.getInstrumentation().context

    lateinit var database: DefaultDatabase

    @Before
    @CallSuper
    open fun setUp() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)
    }

    @Before
    @CallSuper
    open fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }

    fun mockPubNub() {

        val configuration: PNConfiguration = mockk(relaxed = true, relaxUnitFun = true)
        every { configuration.userId } returns com.pubnub.api.UserId(userId)

        pubNub = mockk(relaxed = true, relaxUnitFun = true)
        every { pubNub!! getProperty "configuration" } returns configuration
    }

    fun unmockPubNub() {
        pubNub = null
    }

    fun mockDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mockkObject(Database)

        database = Room.inMemoryDatabaseBuilder(context, DefaultDatabase::class.java)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)


                    GlobalScope.launch(Dispatchers.IO) {
                        with(database) {
                            // add test member
                            memberDao().insertOrUpdate(
                                DBMember(
                                    id = userId,
                                    name = "test",
                                    custom = DBMember.CustomData("asd"),
                                ),
                            )
                        }
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .build()

        every { Database.initialize(any(), any()) } returns database
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