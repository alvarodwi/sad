package me.varoa.sad.ui.screen.story.list

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import me.varoa.sad.core.domain.model.Story
import me.varoa.sad.core.domain.repository.AuthRepository
import me.varoa.sad.core.domain.repository.StoryRepository
import me.varoa.sad.ui.base.BaseViewModel
import me.varoa.sad.ui.screen.auth.AuthEvent
import javax.inject.Inject

@HiltViewModel
class ListStoryViewModel @Inject constructor(
  private val auth: AuthRepository,
  private val story: StoryRepository
) : BaseViewModel() {
  val stories: Flow<PagingData<Story>> =
    story.getStories()
      .cachedIn(viewModelScope)

  fun onLogout() {
    viewModelScope.launch {
      auth.logout()
      sendNewEvent(AuthEvent.LoggedOut)
    }
  }
}