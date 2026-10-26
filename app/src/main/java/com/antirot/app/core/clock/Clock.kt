package com.antirot.app.core.clock

/**
 * Injectable clock abstraction. All domain code uses this; never call
 * System.currentTimeMillis() outside this package.
 */
interface Clock {
    /** Current epoch millis. */
    fun now(): Long
}
