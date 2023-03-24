package me.varoa.sad.ui.screen.story.list

import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.varoa.sad.core.domain.repository.AuthRepository
import me.varoa.sad.core.domain.repository.StoryRepository
import me.varoa.sad.utils.DataDummy
import me.varoa.sad.utils.TestPagingSource
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ListStoryViewModelTest {
    // yes i know this isn't scalable but I'm only making 1 unit test so why not
    // https://stackoverflow.com/questions/58303961/kotlin-coroutine-unit-test-fails-with-module-with-the-main-dispatcher-had-faile
    private val dispatcher = UnconfinedTestDispatcher()

    @MockK
    lateinit var auth: AuthRepository

    @MockK
    lateinit var story: StoryRepository

    private lateinit var viewModel: ListStoryViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `fetch story list from api successfully`() = runTest {
        val dataDummy = DataDummy.generateDummyStories(10)
        coEvery { story.getStories() } returns flow {
            emit(TestPagingSource.snapshot(dataDummy))
        }
        viewModel = ListStoryViewModel(auth, story)
        coVerify { story.getStories() }

        val actualPagingData = viewModel.stories
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.STORY_DIFF,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = dispatcher,
            workerDispatcher = dispatcher
        )
        actualPagingData.test {
            differ.submitData(awaitItem())
        }

        advanceUntilIdle()

        // assert paging data not null
        Assert.assertNotNull(differ.snapshot())
        // assert paging data size is the same
        Assert.assertEquals(dataDummy.size, differ.snapshot().size)
        // assert first data is the same
        Assert.assertEquals(dataDummy[0], differ.snapshot()[0])
    }

    @Test
    fun `fetch story list from api returning 0 items`() = runTest {
        coEvery { story.getStories() } returns flow {
            emit(TestPagingSource.snapshot(emptyList()))
        }
        viewModel = ListStoryViewModel(auth, story)
        coVerify { story.getStories() }

        val actualPagingData = viewModel.stories
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.STORY_DIFF,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = dispatcher,
            workerDispatcher = dispatcher
        )
        actualPagingData.test {
            differ.submitData(awaitItem())
        }

        advanceUntilIdle()

        // assert paging data not null
        Assert.assertNotNull(differ.snapshot())
        // assert paging data size is 0
        Assert.assertEquals(0, differ.snapshot().size)
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
