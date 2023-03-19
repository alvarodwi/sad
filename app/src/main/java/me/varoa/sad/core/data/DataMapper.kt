package me.varoa.sad.core.data

import me.varoa.sad.core.data.remote.json.StoryJson
import me.varoa.sad.core.data.remote.json.request.LoginBodyJson
import me.varoa.sad.core.data.remote.json.request.RegisterBodyJson
import me.varoa.sad.core.domain.model.Auth
import me.varoa.sad.core.domain.model.NewStory
import me.varoa.sad.core.domain.model.Story
import me.varoa.sad.ui.parcelable.ParcelableStory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun StoryJson.toModel() =
  Story(
    id = id,
    name = name,
    description = description,
    photoUrl = photoUrl,
    createdAt = createdAt,
    lat = lat,
    lon = lon
  )

fun NewStory.toMultipart(): Pair<Map<String, RequestBody>, MultipartBody.Part> = Pair(
  HashMap<String, RequestBody>().apply {
    put("description", description.toRequestBody("text/plain".toMediaType()))
    lat?.let { put("lat", it.toString().toRequestBody("text/plain".toMediaType())) }
    lon?.let { put("lon", it.toString().toRequestBody("text/plain".toMediaType())) }
  },
  MultipartBody.Part.createFormData(
    "photo",
    photo.name,
    photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
  )
)

fun Auth.toLoginBody() =
  LoginBodyJson(
    email = email,
    password = password
  )

fun Auth.toRegisterBody() =
  RegisterBodyJson(
    name = name,
    email = email,
    password = password,
  )

fun Story.parcelize() =
  ParcelableStory(
    id = id,
    name = name,
    description = description,
    photoUrl = photoUrl,
    createdAt = createdAt,
    lat = lat,
    lon = lon
  )

fun ParcelableStory.toModel() =
  Story(
    id = id,
    name = name,
    description = description,
    photoUrl = photoUrl,
    createdAt = createdAt,
    lat = lat,
    lon = lon
  )