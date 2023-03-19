package me.varoa.sad.ui.screen.story.detail

import android.os.Build
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import coil.request.ImageRequest
import me.varoa.sad.R
import me.varoa.sad.core.data.toModel
import me.varoa.sad.core.domain.model.Story
import me.varoa.sad.databinding.FragmentDetailStoryBinding
import me.varoa.sad.ui.base.BaseFragment
import me.varoa.sad.ui.ext.formatDateString
import me.varoa.sad.ui.ext.snackbar
import me.varoa.sad.ui.ext.viewBinding
import me.varoa.sad.ui.parcelable.ParcelableStory

class DetailStoryFragment : BaseFragment(R.layout.fragment_detail_story) {
    private val binding by viewBinding<FragmentDetailStoryBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun bindView() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        try {
            val story = if (Build.VERSION.SDK_INT >= 33) {
                requireArguments().getParcelable("story", ParcelableStory::class.java)
            } else {
                @Suppress("DEPRECATION")
                requireArguments().getParcelable("story")
            } ?: throw IllegalStateException("Story corrupted")

            loadStory(story.toModel())
        } catch (ex: IllegalStateException) {
            modifyErrorLayout("Error : ${ex.message}", "🙏")
            snackbar("Error : ${ex.message}")
            toggleErrorLayout(true)
        }
    }

    private fun loadStory(story: Story) {
        binding.tvDetailDescription.text = story.description
        binding.tvDetailName.text = getString(R.string.lbl_username, story.name)
        binding.tvDetailDate.text = getString(
            R.string.lbl_created_at,
            formatDateString(story.createdAt, "yyyy.MM.dd - HH:mm:ss")
        )
        binding.ivDetailPhoto.apply {
            val imgData = ImageRequest.Builder(this.context)
                .data(story.photoUrl)
                .target(this)
                .allowHardware(true)
                .build()
            imageLoader.enqueue(imgData)
        }
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
