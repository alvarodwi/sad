package me.varoa.sad.ui.screen.story

import me.varoa.sad.ui.base.BaseEvent

sealed class StoryEvent : BaseEvent() {
  object StoryAdded : StoryEvent()
}