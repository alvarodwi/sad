package me.varoa.sad.ui.screen.story.detail

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import coil.request.ImageRequest
import me.varoa.sad.R
import me.varoa.sad.core.data.toModel
import me.varoa.sad.core.domain.model.Story
import me.varoa.sad.databinding.FragmentDetailStoryBinding
import me.varoa.sad.ui.base.BaseFragment
import me.varoa.sad.ui.ext.formatDateString
import me.varoa.sad.ui.ext.viewBinding

class DetailStoryFragment : BaseFragment(R.layout.fragment_detail_story) {
    private val binding by viewBinding<FragmentDetailStoryBinding>()
    private val args by navArgs<DetailStoryFragmentArgs>()

    override fun bindView() {
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        postponeEnterTransition()

        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
            tvDetailName.transitionName = "name-${args.story.id}"
            ivDetailPhoto.transitionName = "photo-${args.story.id}"
        }

        loadStory(args.story.toModel())
    }

    private fun loadStory(story: Story) {
        with(binding) {
            tvDetailDescription.text = story.description
            tvDetailName.text = getString(R.string.lbl_username, story.name)
            tvDetailDate.text = getString(
                R.string.lbl_created_at,
                formatDateString(story.createdAt, "yyyy.MM.dd - HH:mm:ss")
            )
            ivDetailPhoto.apply {
                val imgData = ImageRequest.Builder(this.context)
                    .data(story.photoUrl)
                    .target(this)
                    .allowHardware(true)
                    .listener(
                        onSuccess = { _, _ ->
                            startPostponedEnterTransition()
                        },
                        onError = { _, _ ->
                            startPostponedEnterTransition()
                        }
                    )
                    .build()
                imageLoader.enqueue(imgData)
            }
        }
    }
}
