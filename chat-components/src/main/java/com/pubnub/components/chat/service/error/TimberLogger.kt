package com.pubnub.components.chat.service.error

import com.pubnub.framework.service.error.Logger
import timber.log.Timber

/**
 * Logger implementation with Timber usage
 *
 * @param trees List of facades for handling logging calls. By default Timber.DebugTree().
 */
class TimberLogger(vararg trees: Timber.Tree = arrayOf(DebugTree())) : Logger {

    companion object {
        private var initialized = false
    }

    init {
        if (!initialized and trees.isNotEmpty()) {
            initialized = true
            Timber.plant(*trees)
        }
    }

    override fun log(priority: Int, t: Throwable?, message: String?, vararg args: Any?) {
        Timber.log(priority, t, message, args)
    }

    /**
     * DebugTree with custom ignored class names
     */
    class DebugTree : Timber.DebugTree() {
        private val customFqcnIgnore = listOf(
            Timber::class.java.name,
            Timber.Forest::class.java.name,
            Timber.Tree::class.java.name,
            Timber.DebugTree::class.java.name,
            TimberLogger::class.java.name,
            DebugTree::class.java.name,
            Logger::class.java.name,
            Logger::class.java.name+"\$DefaultImpls",
        )

        /**
         * Override ignored class names by reflection
         */
        init {
            val f = this::class.java.superclass.getDeclaredField("fqcnIgnore")
            f.isAccessible = true
            f.set(this, customFqcnIgnore)
        }
    }
}
