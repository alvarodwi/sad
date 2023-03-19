package me.varoa.sad.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import me.varoa.sad.core.data.AuthRepositoryImpl
import me.varoa.sad.core.data.StoryRepositoryImpl
import me.varoa.sad.core.domain.repository.AuthRepository
import me.varoa.sad.core.domain.repository.StoryRepository

@Module
@InstallIn(ViewModelComponent::class)
interface DomainModule {
    @Binds
    fun authRepository(repo: AuthRepositoryImpl): AuthRepository

    @Binds
    fun storyRepository(repo: StoryRepositoryImpl): StoryRepository
}
