package me.varoa.sad.ui.screen.story.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import me.varoa.sad.core.domain.model.Story
import me.varoa.sad.core.domain.repository.StoryRepository
import me.varoa.sad.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DetailStoryViewModel @Inject constructor(
    private val story: StoryRepository,
    handle: SavedStateHandle
) : BaseViewModel() {
    private val id = handle.get<String>("id") ?: ""

    private val _detailStory = MutableSharedFlow<Story>()
    val detailStory get() = _detailStory.asSharedFlow()

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            story.getStory(id)
                .catch { showErrorMessage(it.message ?: "") }
                .collect { result ->
                    if (result.isSuccess) {
                        _detailStory.emit(result.getOrThrow())
                    } else if (result.isFailure) showErrorMessage(result.exceptionOrNull()?.message)
                }
        }
    }
}
