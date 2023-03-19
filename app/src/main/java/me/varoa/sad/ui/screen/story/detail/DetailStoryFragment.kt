package me.varoa.sad.ui.screen.story.detail

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import coil.request.ImageRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import me.varoa.sad.R
import me.varoa.sad.core.domain.model.Story
import me.varoa.sad.databinding.FragmentDetailStoryBinding
import me.varoa.sad.ui.base.BaseEvent.ShowErrorMessage
import me.varoa.sad.ui.base.BaseFragment
import me.varoa.sad.ui.ext.snackbar
import me.varoa.sad.ui.ext.viewBinding

class DetailStoryFragment : BaseFragment(R.layout.fragment_detail_story) {
  private val binding by viewBinding<FragmentDetailStoryBinding>()
  private val viewModel by viewModels<DetailStoryViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    sharedElementEnterTransition =
      TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
  }

  override fun onStart() {
    super.onStart()
    eventJob = viewModel.events
      .onEach { event ->
        when (event) {
          is ShowErrorMessage -> {
            toggleLoading(false)
            logcat { "Error : ${event.message}" }
            modifyErrorLayout("Error : ${event.message}", "🙏")
            snackbar("Error : ${event.message}")
            toggleErrorLayout(true)
          }
        }
      }.launchIn(viewLifecycleOwner.lifecycleScope)
  }

  override fun bindView() {
    binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.detailStory.collectLatest(::loadStory)
    }

    binding.swipeRefresh.setOnRefreshListener { viewModel.onRefresh() }
    toggleLoading(true)
  }

  private fun loadStory(story: Story) {
    toggleLoading(false)
    binding.tvDetailDescription.text = story.description
    binding.tvDetailName.text = getString(R.string.lbl_username, story.name)
    binding.tvDetailDate.text = getString(R.string.lbl_created_at, story.createdAt)
    binding.ivDetailPhoto.apply {
      val imgData = ImageRequest.Builder(this.context)
        .data(story.photoUrl)
        .target(this)
        .allowHardware(true)
        .build()
      imageLoader.enqueue(imgData)
    }
  }

  private fun toggleLoading(isLoading: Boolean) {
    binding.swipeRefresh.isRefreshing = isLoading
  }

  private fun modifyErrorLayout(message: String, emoji: String) {
    binding.layoutError.apply {
      tvMessage.text = message
      tvEmoji.text = emoji
    }
  }

  private fun toggleErrorLayout(flag: Boolean) {
    binding.layoutError.root.isVisible = flag
    binding.ivDetailPhoto.isVisible = !flag
    binding.tvDetailName.isVisible = !flag
    binding.cardInfo.isVisible = !flag
  }
}