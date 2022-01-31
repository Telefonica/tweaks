package com.telefonica.tweaks.domain

import com.telefonica.tweaks.data.TweaksDataStore
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock

class TweaksBusinessLogicTest {

    private val tweaksDataStore: TweaksDataStore = mock()
    private val sut: TweaksBusinessLogic = TweaksBusinessLogic(tweaksDataStore)

    @Test
    fun `a tweak added as a cover is in the graph`() = runBlocking {
        val graph = givenATweaksGraphThatHasACover()

        sut.initialize(graph)

        assertEquals(A_COVER_ENTRY_VALUE, sut.getValue<String>(A_COVER_ENTRY_KEY).first())
    }

    private fun givenATweaksGraphThatHasACover(): TweaksGraph = tweaksGraph {
        cover(COVER_TITLE) {
            label(A_COVER_ENTRY_KEY, A_COVER_ENTRY_NAME) { flowOf(A_COVER_ENTRY_VALUE) }
        }
    }

    companion object {
        private const val A_COVER_ENTRY_KEY = "cover-entry-key"
        private const val A_COVER_ENTRY_NAME = "Cover entry name"
        private const val A_COVER_ENTRY_VALUE = "Cover entry value"
        private const val COVER_TITLE = "Cover"
    }
}