package me.varoa.sad.ui.screen.maps

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat
import me.varoa.sad.core.domain.model.Story
import me.varoa.sad.core.domain.repository.StoryRepository
import me.varoa.sad.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val story: StoryRepository
) : BaseViewModel() {
    private val currentPage = MutableStateFlow(1)
    private val _stories = MutableStateFlow<List<Story>>(listOf())
    val stories get() = _stories.asStateFlow()

    init {
        onLoadMore()
    }

    fun onLoadMore() {
        viewModelScope.launch {
            val currentStories = _stories.value
            story.getStoriesWithLocation(currentPage.value)
                .catch { showErrorMessage(it.message) }
                .collectLatest { result ->
                    if (result.isSuccess) {
                        val data = result.getOrThrow()
                        if (data.isEmpty()) {
                            sendNewEvent(MapEvent.EndOfPagination)
                        }
                        _stories.emit((currentStories + data).distinct())
                    } else if (result.isFailure) {
                        showErrorMessage(result.exceptionOrNull()?.message)
                    }
                }
            logcat { "mapsData -> page = ${currentPage.value} and totalItems = ${stories.value.size}" }
            currentPage.emit(currentPage.value.plus(1))
        }
    }
}
