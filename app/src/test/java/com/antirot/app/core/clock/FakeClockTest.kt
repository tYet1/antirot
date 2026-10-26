package com.antirot.app.core.clock

import org.junit.Assert.assertEquals
import org.junit.Test

class FakeClockTest {

    @Test
    fun `initial time is returned`() {
        val clock = FakeClock(1_000L)
        assertEquals(1_000L, clock.now())
    }

    @Test
    fun `advanceBy increments time correctly`() {
        val clock = FakeClock(0L)
        clock.advanceBy(5_000L)
        assertEquals(5_000L, clock.now())
    }

    @Test
    fun `multiple advances accumulate`() {
        val clock = FakeClock(0L)
        clock.advanceBy(1_000L)
        clock.advanceBy(2_000L)
        assertEquals(3_000L, clock.now())
    }
}
