package me.varoa.sad.ui.screen.maps

import me.varoa.sad.ui.base.BaseEvent

sealed class MapEvent : BaseEvent() {
  object EndOfPagination : MapEvent()
}