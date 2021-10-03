package com.framstag.taskdown

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CollectionTest {

    @Test
    fun testExceptionHandlingNaive() {
        val list = listOf("eins", "zwei", "drei")

        val exception = Assertions.assertThrows(Exception::class.java) {
            list.map {
                if (it=="zwei") {
                    throw Exception("Zwei ist nicht erlaubt")
                }
                else {
                    it.uppercase()
                }
            }
        }

        Assertions.assertEquals("Zwei ist nicht erlaubt",exception.message)
    }

    @Test
    fun testExceptionHandlingNull() {
        val list = listOf("eins", "zwei", "drei")

        val result = list.mapNotNull {
            if (it=="zwei") {
                null
            }
            else {
                it.uppercase()
            }
        }

        Assertions.assertEquals(listOf("EINS", "DREI"),result)
    }

    @Test
    fun testExceptionHandlingResult() {
        val list = listOf("eins", "zwei", "drei")
        val exception = Exception("Zwei ist nicht erlaubt")

        val (successResults, failureResults) = list.map {
            if (it=="zwei") {
                Result.failure(exception)
            }
            else {
                Result.success(it.uppercase())
            }
        }.partition {
            it.isSuccess
        }

        val successes = successResults.map {
            it.getOrThrow()
        }

        val failures = failureResults.map {
            it.exceptionOrNull()
        }

        Assertions.assertEquals(listOf("EINS","DREI"),successes)
        Assertions.assertEquals(listOf(exception), failures)
    }
}