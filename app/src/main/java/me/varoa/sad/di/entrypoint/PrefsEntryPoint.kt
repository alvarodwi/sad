package me.varoa.sad.di.entrypoint

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.varoa.sad.core.data.prefs.DataStoreManager

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PrefsEntryPoint {
  /**
   * @return
   */
  fun prefs(): DataStoreManager
}