package me.varoa.sad.ui.parcelable

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableStory(
  val id: String,
  val name: String,
  val description: String,
  val photoUrl: String,
  val createdAt: String,
  val lat: Float? = null,
  val lon: Float? = null,
) : Parcelable