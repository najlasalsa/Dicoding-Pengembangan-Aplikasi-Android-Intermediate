package com.example.appstory.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appstory.databinding.StoriesLoadBinding

class LoadingPagingAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingPagingAdapter.LoadingViewHolder>() {
    inner class LoadingViewHolder(private val binding: StoriesLoadBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.error.text = loadState.error.localizedMessage
            }
            binding.apply {
                loadBar.isVisible = loadState is LoadState.Loading
                retry.isVisible = loadState is LoadState.Error
                error.isVisible = loadState is LoadState.Error
            }
        }
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingViewHolder {
        val binding = StoriesLoadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }
}