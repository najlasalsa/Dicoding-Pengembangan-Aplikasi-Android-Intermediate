package com.example.appstory.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appstory.R
import com.example.appstory.StoryViewModel
import com.example.appstory.StoryViewModelFactory
import com.example.appstory.data.UserPreference
import com.example.appstory.data.ViewModelFactory
import com.example.appstory.databinding.ActivityMainBinding
import com.example.appstory.login.SignInActivity
import com.example.appstory.maps.MapsActivity
import com.example.appstory.paging.LoadingPagingAdapter
import com.example.appstory.story.NewStoryActivity
import com.example.appstory.story.StoriesAdapter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val storyViewModel: StoryViewModel by viewModels { StoryViewModelFactory(this) }
    private lateinit var adapter: StoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAdapter()
        setupAction()
        setupUser()
    }

    private fun setupLayout() {
        binding.apply {
            rvUserStories.layoutManager = LinearLayoutManager(this@MainActivity)
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                rvUserStories.layoutManager = GridLayoutManager(this@MainActivity, 2)
            } else {
                rvUserStories.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        viewModel.isLoad.observe(this) {
            showLoad(it)
        }
        viewModel.message.observe(this) { response ->
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupAdapter() {
        adapter = StoriesAdapter()
        setupLayout()
        binding.rvUserStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingPagingAdapter {
                adapter.retry()
            }
        )
    }

    private fun setupAction() {
        binding.addStory.setOnClickListener {
            startActivity(Intent(this, NewStoryActivity::class.java))
            finish()
        }
    }

    private fun setupUser() {
        viewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                storyViewModel.story.observe(this) {
                    adapter.submitData(lifecycle, it)
                }
            } else {
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
    }

    private fun showLoad(isLoad: Boolean) {
        binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.language -> {
                val intentToLang = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intentToLang)
                finish()
                true
            }
            R.id.logout -> {
                viewModel.userLogout()
                true
            }
            R.id.maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                finish()
                true
            }
            else -> false
        }
    }
}