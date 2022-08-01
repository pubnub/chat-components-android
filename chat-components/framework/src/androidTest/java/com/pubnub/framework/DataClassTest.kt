package com.pubnub.framework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pubnub.framework.util.data.NotADataClassException
import com.pubnub.framework.util.data.clone
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DataClassTest {

    @Test
    fun givenDataClassWithInterface_whenCloneIsCalledWithoutVarargParameters_thenInstanceIsTheSame() {
        val fake = FakeData("ID", "Test", 12, "Custom")
        val int = fake as FakeInterface
        val duplicated = clone(int)
        Assert.assertEquals(fake, duplicated)
    }

    @Test
    fun givenDataClassWithInterface_whenCloneIsCalledWithSomeParameters_thenNewInstanceHavePassedParametersAndRestFromOriginalInstance() {
        val fake = FakeData("ID", "Test", 12, "Custom")
        val int = fake as FakeInterface
        val duplicated = clone(int, fake::id.name to "ID2", fake::custom.name to null)
        Assert.assertEquals("ID2", duplicated.id)
        Assert.assertEquals(null, duplicated.custom)
    }

    @Test(expected = NotADataClassException::class)
    fun givenClassWithInterface_whenCloneIsCalled_thenExceptionIsThrown() {
        val fake = FakeClass("ID", "Test", 12, "Custom")
        val int = fake as FakeInterface
        clone(int)
    }

    interface FakeInterface {
        val id: String
        val name: String
        val count: Long
        val custom: Any?
    }

    data class FakeData(
        override val id: String,
        override val name: String,
        override val count: Long,
        override val custom: Any?,
    ) : FakeInterface

    class FakeClass(
        override val id: String,
        override val name: String,
        override val count: Long,
        override val custom: Any?,
    ) : FakeInterface
}