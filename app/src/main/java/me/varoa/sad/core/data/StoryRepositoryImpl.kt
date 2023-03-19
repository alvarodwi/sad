package me.varoa.sad.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import me.varoa.sad.core.data.paging.StoryPagingSource
import me.varoa.sad.core.data.prefs.DataStoreManager
import me.varoa.sad.core.data.remote.SafeApiRequest
import me.varoa.sad.core.data.remote.api.StoryApiService
import me.varoa.sad.core.data.remote.json.response.GenericResponseJson
import me.varoa.sad.core.domain.model.NewStory
import me.varoa.sad.core.domain.model.Story
import me.varoa.sad.core.domain.repository.StoryRepository
import me.varoa.sad.utils.ApiException
import me.varoa.sad.utils.NoInternetException
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val api: StoryApiService,
    private val prefs: DataStoreManager
) : StoryRepository, SafeApiRequest() {

    override fun getStories(): Flow<PagingData<Story>> =
        prefs.sessionToken.flatMapLatest { token ->
            Pager(
                PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = true)
            ) {
                StoryPagingSource(api, token)
            }.flow
        }

    override suspend fun getStory(id: String): Flow<Result<Story>> = flow {
        try {
            val token = prefs.sessionToken.first()
            val story = apiRequest(
                { api.getStoryDetail(token, id) },
                ::decodeErrorJson
            ).story.toModel()
            emit(Result.success(story))
        } catch (ex: ApiException) {
            emit(Result.failure(ex))
        } catch (ex: NoInternetException) {
            emit(Result.failure(ex))
        }
    }

    override suspend fun postNewStory(
        data: NewStory,
        isGuest: Boolean
    ): Flow<Result<String>> = flow {
        try {
            val token = prefs.sessionToken.first()
            val multipart = data.toMultipart()
            val response = apiRequest(
                {
                    if (isGuest) {
                        api.postNewStory(multipart.first, multipart.second)
                    } else {
                        api.postNewStory(token, multipart.first, multipart.second)
                    }
                },
                ::decodeErrorJson
            )
            emit(Result.success(response.message))
        } catch (ex: ApiException) {
            emit(Result.failure(ex))
        } catch (ex: NoInternetException) {
            emit(Result.failure(ex))
        }
    }

    private fun decodeErrorJson(str: String): String =
        Json.decodeFromString(GenericResponseJson.serializer(), str).message
}
