package com.pubnub.framework.service.error

import android.util.Log
import org.jetbrains.annotations.NonNls

interface Logger {

    /** Log a verbose exception and a message with optional format args */
    fun v(t: Throwable?, @NonNls message: String?, vararg args: Any?) { log(priority = Log.VERBOSE, t = t, message = message, args = args) }

    /** Log a verbose message with optional format args */
    fun v(@NonNls message: String?, vararg args: Any?) { v(t = null, message = message, args = args) }

    /** Log a verbose exception */
    fun v(t: Throwable?) { v(t = t, message = null) }

    /** Log a debug exception and a message with optional format args */
    fun d(t: Throwable?, @NonNls message: String?, vararg args: Any?) { log(priority = Log.DEBUG, t = t, message = message, args = args) }

    /** Log a debug message with optional format args */
    fun d(@NonNls message: String?, vararg args: Any?) { d(t = null, message = message, args = args) }

    /** Log a debug exception */
    fun d(t: Throwable?) { d(t = t, message =null) }

    /** Log an info exception and a message with optional format args */
    fun i(t: Throwable?, @NonNls message: String?, vararg args: Any?) { log(priority = Log.INFO, t = t, message = message, args = args) }

    /** Log an info message with optional format args */
    fun i(@NonNls message: String?, vararg args: Any?) { i(t = null, message = message, args = args) }

    /** Log an info exception */
    fun i(t: Throwable?) { i(t = t, message =null) }

    /** Log a warning exception and a message with optional format args */
    fun w(t: Throwable?, @NonNls message: String?, vararg args: Any?) { log(priority = Log.WARN, t = t, message = message, args = args) }

    /** Log a warning message with optional format args */
    fun w(@NonNls message: String?, vararg args: Any?) { w(t = null, message = message, args = args) }

    /** Log a warning exception */
    fun w(t: Throwable?) { w(t = t, message =null) }

    /** Log an error exception and a message with optional format args */
    fun e(t: Throwable?, @NonNls message: String?, vararg args: Any?) { log(priority = Log.ERROR, t = t, message = message, args = args) }

    /** Log an error message with optional format args */
    fun e(@NonNls message: String?, vararg args: Any?) { e(t = null, message = message, args = args) }

    /** Log an error exception */
    fun e(t: Throwable?) { e(t = t, message =null) }

    /** Log an assert exception and a message with optional format args */
    fun wtf(t: Throwable?, @NonNls message: String?, vararg args: Any?) { log(priority = Log.ASSERT, t = t, message = message, args = args) }

    /** Log an assert message with optional format args */
    fun wtf(@NonNls message: String?, vararg args: Any?) { wtf(t = null, message = message, args = args) }

    /** Log an assert exception */
    fun wtf(t: Throwable?) { wtf(t = t, message =null) }

    /** Log at `priority` an exception and a message with optional format args */
    fun log(priority: Int, t: Throwable?, @NonNls message: String?, vararg args: Any?)

    /** Log at `priority` a message with optional format args */
    fun log(priority: Int, @NonNls message: String?, vararg args: Any?) { log(priority = priority, t = null, message = message, args = args) }

    /** Log at `priority` an exception */
    fun log(priority: Int, t: Throwable?) { log(priority = priority, t = t, message = null) }
}