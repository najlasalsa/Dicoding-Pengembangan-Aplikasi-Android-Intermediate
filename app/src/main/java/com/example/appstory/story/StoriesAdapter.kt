package com.example.appstory.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appstory.ListStoryItem
import com.example.appstory.R
import com.example.appstory.databinding.UserStoriesBinding
import com.example.appstory.story.DetailStoriesActivity.Companion.DETAIL_STORIES

class StoriesAdapter :
    PagingDataAdapter<ListStoryItem, StoriesAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: UserStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stories: ListStoryItem) {
            binding.apply {
                Glide.with(itemView)
                    .load(stories.photoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(userStory)
                userName.text = stories.name
                storyDesc.text = stories.description
                itemView.setOnClickListener {
                    val intentToDetail = Intent(itemView.context, DetailStoriesActivity::class.java)
                    intentToDetail.putExtra(DETAIL_STORIES, stories)

                    val options: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(userName, "Name"),
                            Pair(userStory, "Picture"),
                            Pair(storyDesc, "Description")
                        )
                    itemView.context.startActivity(
                        intentToDetail, options.toBundle()
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = UserStoriesBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null)
            holder.bind(story)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.description == newItem.description &&
                        oldItem.photoUrl == newItem.photoUrl
            }
        }
    }
}