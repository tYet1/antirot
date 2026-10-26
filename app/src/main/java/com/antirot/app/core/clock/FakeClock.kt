package com.antirot.app.core.clock

/**
 * Test-only clock. Advance time with [advanceBy] or set it directly via [currentTime].
 */
class FakeClock(initialTime: Long = 0L) : Clock {
    var currentTime: Long = initialTime

    override fun now(): Long = currentTime

    fun advanceBy(millis: Long) {
        currentTime += millis
    }
}
