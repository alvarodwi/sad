package me.varoa.sad.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import logcat.logcat
import me.varoa.sad.BuildConfig
import me.varoa.sad.core.data.prefs.DataStoreManager
import me.varoa.sad.core.data.remote.NoConnectionInterceptor
import me.varoa.sad.core.data.remote.api.StoryApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    companion object {
        const val DEFAULT_TIMEOUT = 5L

        var BASE_URL = "https://story-api.dicoding.dev/v1/"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Singleton
    @Provides
    fun provideNoConnectionInterceptor(
        @ApplicationContext appContext: Context
    ): NoConnectionInterceptor =
        NoConnectionInterceptor(appContext)

    @Singleton
    @Provides
    fun provideHttpClient(netConn: NoConnectionInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES)

        // logging
        if (BuildConfig.DEBUG) {
            logcat { "Applying HTTP Logging" }
            val logger = HttpLoggingInterceptor { message ->
                logcat("API") { message }
            }.apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            builder.addInterceptor(logger)
        }

        // detect internet connection
        builder.addInterceptor(netConn)
        return builder
            .build()
    }

    @ExperimentalSerializationApi
    @Singleton
    @Provides
    fun provideJsonConverterFactory(): Converter.Factory {
        return json.asConverterFactory("application/json".toMediaType())
    }

    @Singleton
    @Provides
    fun provideStoryApiService(client: OkHttpClient, factory: Converter.Factory): StoryApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(factory)
            .build()
        return retrofit.create(StoryApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppPreferences(@ApplicationContext appContext: Context): DataStoreManager =
        DataStoreManager(appContext)
}
