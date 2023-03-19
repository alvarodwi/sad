package me.varoa.sad.core.data.remote.api

import me.varoa.sad.core.data.remote.json.request.LoginBodyJson
import me.varoa.sad.core.data.remote.json.request.RegisterBodyJson
import me.varoa.sad.core.data.remote.json.response.GenericResponseJson
import me.varoa.sad.core.data.remote.json.response.ListStoryResponseJson
import me.varoa.sad.core.data.remote.json.response.LoginResponseJson
import me.varoa.sad.core.data.remote.json.response.StoryResponseJson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface StoryApiService {
    @POST("register")
    suspend fun register(
        @Body body: RegisterBodyJson
    ): Response<GenericResponseJson>

    @POST("login")
    suspend fun login(
        @Body body: LoginBodyJson
    ): Response<LoginResponseJson>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") auth: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 0
    ): Response<ListStoryResponseJson>

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ): Response<StoryResponseJson>

    @JvmSuppressWildcards
    @Multipart
    @POST("stories")
    suspend fun postNewStory(
        @Header("Authorization") auth: String,
        @PartMap map: Map<String, RequestBody>,
        @Part photo: MultipartBody.Part
    ): Response<GenericResponseJson>

    @JvmSuppressWildcards
    @Multipart
    @POST("stories/guest")
    suspend fun postNewStory(
        @PartMap map: Map<String, RequestBody>,
        @Part photo: MultipartBody.Part
    ): Response<GenericResponseJson>
}
