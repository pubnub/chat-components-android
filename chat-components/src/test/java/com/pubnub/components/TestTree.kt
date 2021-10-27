package com.pubnub.components

import timber.log.Timber

class TestTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        println("$priority: [$tag] $message, $t")
    }
}
