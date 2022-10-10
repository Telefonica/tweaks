package com.telefonica.tweaks.domain

import com.telefonica.tweaks.data.TweaksRepository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class TweaksBusinessLogicTest {

    private val tweaksRepository: TweaksRepository = mock()
    private val sut: TweaksBusinessLogic = TweaksBusinessLogic(tweaksRepository)

    @Test
    fun `a tweak added as a cover is in the graph`() = runBlocking {
        val graph = givenATweaksGraphThatHasACover()

        sut.initialize(graph)

        val result = sut.getValue<String>(A_COVER_ENTRY_KEY).first()
        assertEquals(A_COVER_ENTRY_VALUE, result)
    }

    private fun givenATweaksGraphThatHasACover(): TweaksGraph = tweaksGraph {
        cover(COVER_TITLE) {
            editableString(key = A_COVER_ENTRY_KEY, name = A_COVER_ENTRY_NAME, defaultValue = A_COVER_ENTRY_VALUE)
        }

        whenever(tweaksRepository.isOverriden(any())).thenReturn(flowOf(false))
    }

    companion object {
        private const val A_COVER_ENTRY_KEY = "cover-entry-key"
        private const val A_COVER_ENTRY_NAME = "Cover entry name"
        private const val A_COVER_ENTRY_VALUE = "Cover entry value"
        private const val COVER_TITLE = "Cover"
    }
}