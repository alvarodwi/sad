package me.varoa.sad.utils

import me.varoa.sad.core.domain.model.Story

object DataDummy {
  fun generateDummyStories(n: Int): List<Story> {
    val stories = arrayListOf<Story>()
    for (i in 1..n) {
      stories.add(
        Story(
          id = "story-$i",
          name = "Test",
          description = "",
          photoUrl = "",
          createdAt = "",
          lat = null,
          lon = null
        )
      )
    }
    return stories
  }
}