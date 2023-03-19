package me.varoa.sad.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import me.varoa.sad.databinding.ItemLoadStateBinding
import me.varoa.sad.ui.adapter.GenericLoadStateAdapter.LoadStateViewHolder
import me.varoa.sad.ui.ext.viewBinding

class GenericLoadStateAdapter(
  private val retry: () -> Unit
) : LoadStateAdapter<LoadStateViewHolder>() {
  inner class LoadStateViewHolder(private val binding: ItemLoadStateBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(loadState: LoadState) {
      with(binding) {
        btnRetry.setOnClickListener {
          retry
        }
        if (loadState is LoadState.Error) {
          tvErrorMessage.text = loadState.error.localizedMessage
        }
        progressBar.isVisible = loadState is LoadState.Loading
        btnRetry.isVisible = loadState is LoadState.Error
        tvErrorMessage.isVisible = loadState is LoadState.Error
      }
    }
  }

  override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) =
    holder.bind(loadState)

  override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
    LoadStateViewHolder(parent.viewBinding(ItemLoadStateBinding::inflate))
}