package me.varoa.sad.utils

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState

class TestPagingSource<T : Any> : PagingSource<Int, T>() {
    companion object {
        fun <T : Any> snapshot(items: List<T>): PagingData<T> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}
