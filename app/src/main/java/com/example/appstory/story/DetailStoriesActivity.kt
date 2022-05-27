package com.example.appstory.story

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.appstory.ListStoryItem
import com.example.appstory.R
import com.example.appstory.data.UserPreference
import com.example.appstory.data.ViewModelFactory
import com.example.appstory.databinding.ActivityDetailStoriesBinding
import com.example.appstory.main.MainViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class DetailStoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoriesBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(
            this, ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        val stories = intent.getParcelableExtra<ListStoryItem>(DETAIL_STORIES) as ListStoryItem
        binding.apply {
            Glide.with(this@DetailStoriesActivity)
                .load(stories.photoUrl)
                .fitCenter()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(userStory)
            userName.text = stories.name
            storyDesc.text = stories.description
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val DETAIL_STORIES = "detail_stories"
    }
}