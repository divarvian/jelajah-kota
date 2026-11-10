package com.arvian.jelajahkota.core.common.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultTest {

    @Test
    fun success_containsData() {
        val result = Result.Success("Malang Heritage")
        assertEquals("Malang Heritage", result.data)
    }

    @Test
    fun error_containsExceptionAndMessage() {
        val exception = IllegalStateException("Network failed")
        val result = Result.Error(exception)
        assertEquals(exception, result.exception)
        assertEquals("Network failed", result.message)
    }

    @Test
    fun loading_isSingleton() {
        val loading: Result<String> = Result.Loading
        assertTrue(loading is Result.Loading)
    }
}
