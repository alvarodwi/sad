package me.varoa.sad.core.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.varoa.sad.core.domain.model.NewStory
import me.varoa.sad.core.domain.model.Story

interface StoryRepository {
  fun getStories(): Flow<PagingData<Story>>

  suspend fun getStory(id: String): Flow<Result<Story>>

  suspend fun postNewStory(
    data: NewStory,
    isGuest: Boolean = false,
  ): Flow<Result<String>>
}