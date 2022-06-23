package com.pubnub.components

import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.toIsoString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class ISO8601UnitTest {

    @Test
    fun givenTimetokenInMillisX10_whenToIsoStringIsCalled_thenCorrectStringIsReturned() = runTest {
        val timetoken: Timetoken = 16521940800000
        val expectedString = "2022-05-10T14:48:00.000Z"

        Assert.assertEquals(expectedString, timetoken.toIsoString())
    }
}
