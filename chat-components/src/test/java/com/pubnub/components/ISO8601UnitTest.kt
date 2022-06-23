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
        val timetoken: Timetoken = 16521940800000000
        val expectedString = "2022-05-10T14:48:00.000Z"

        Assert.assertEquals(expectedString, timetoken.toIsoString())
    }

    @Test
    fun givenTimetoken_whenToIsoStringIsCalled_thenCorrectStringIsReturned() = runTest {
        val timetoken: Timetoken = 16559952035151748
        val expectedString = "2022-06-23T14:40:03.515Z"

        Assert.assertEquals(expectedString, timetoken.toIsoString())
    }
}
