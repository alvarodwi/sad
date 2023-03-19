package me.varoa.sad.ui.screen.story.add

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import me.varoa.sad.core.domain.model.NewStory
import me.varoa.sad.core.domain.repository.StoryRepository
import me.varoa.sad.ui.base.BaseViewModel
import me.varoa.sad.ui.screen.story.StoryEvent
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
  private val story: StoryRepository
) : BaseViewModel() {
  fun onAddStory(data: NewStory) {
    viewModelScope.launch {
      story.postNewStory(data)
        .catch { showErrorMessage(it.message) }
        .collect { result ->
          if (result.isSuccess) {
            sendNewEvent(StoryEvent.StoryAdded)
          } else {
            showErrorMessage(result.exceptionOrNull()?.message)
          }
        }
    }
  }
}