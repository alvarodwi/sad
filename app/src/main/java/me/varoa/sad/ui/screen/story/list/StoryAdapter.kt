package me.varoa.sad.ui.screen.story.list

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import me.varoa.sad.R
import me.varoa.sad.core.domain.model.Story
import me.varoa.sad.databinding.ItemStoryBinding
import me.varoa.sad.ui.ext.viewBinding
import me.varoa.sad.ui.screen.story.list.StoryAdapter.UserViewHolder

class StoryAdapter(
    private val imageLoader: ImageLoader,
    private val onClick: (Story, Array<View>) -> Unit
) : PagingDataAdapter<Story, UserViewHolder>(STORY_DIFF) {
    companion object {
        val STORY_DIFF = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(
                oldItem: Story,
                newItem: Story
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Story,
                newItem: Story
            ): Boolean = oldItem == newItem
        }
    }

    inner class UserViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story?) {
            if (data == null) return
            with(binding) {
                root.setOnClickListener { onClick(data, arrayOf(binding.tvItemName, binding.ivItemPhoto)) }
                ivItemPhoto.apply {
                    transitionName = "photo-${data.id}"
                    val imgData = ImageRequest.Builder(this.context)
                        .data(data.photoUrl)
                        .target(this)
                        .transformations(RoundedCornersTransformation(16f))
                        .allowHardware(true)
                        .build()
                    imageLoader.enqueue(imgData)
                }
                tvItemName.apply {
                    text = root.context.getString(R.string.lbl_username, data.name)
                    transitionName = "name-${data.id}"
                }
            }
        }
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(parent.viewBinding(ItemStoryBinding::inflate))
}
