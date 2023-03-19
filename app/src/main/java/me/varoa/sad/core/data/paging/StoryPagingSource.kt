package me.varoa.sad.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.varoa.sad.core.data.remote.api.StoryApiService
import me.varoa.sad.core.data.remote.json.StoryJson
import me.varoa.sad.core.data.toModel
import me.varoa.sad.core.domain.model.Story
import retrofit2.HttpException
import java.io.IOException

class StoryPagingSource(
    private val api: StoryApiService,
    private val token: String
) : PagingSource<Int, Story>() {
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        try {
            val nextPage = params.key ?: 1
            val response = api.getStories(auth = token, page = nextPage)
            val pagedResponse = response.body()

            val data: List<Story>? = pagedResponse?.listStory?.map(StoryJson::toModel)

            return LoadResult.Page(
                data = data.orEmpty(),
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (data.isNullOrEmpty()) null else nextPage + 1
            )
        } catch (ex: HttpException) {
            throw IOException(ex)
        } catch (ex: IOException) {
            return LoadResult.Error(ex)
        }
    }
}
