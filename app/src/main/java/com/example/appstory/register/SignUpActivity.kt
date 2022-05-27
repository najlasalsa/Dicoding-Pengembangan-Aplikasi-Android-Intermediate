package com.example.appstory.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.appstory.data.UserPreference
import com.example.appstory.data.ViewModelFactory
import com.example.appstory.customviews.EmailEditText
import com.example.appstory.customviews.NameEditText
import com.example.appstory.customviews.PassEditText
import com.example.appstory.databinding.ActivitySignUpBinding
import com.example.appstory.login.SignInActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private lateinit var nameEditText: NameEditText
    private lateinit var emailEditText: EmailEditText
    private lateinit var passEditText: PassEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        customView()
        playAnim()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {

        viewModel = ViewModelProvider(
            this, ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SignUpViewModel::class.java]

        viewModel.isLoad.observe(this) {
            showLoad(it)
        }

        viewModel.message.observe(this) { response ->
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }

    private fun customView() {
        nameEditText = binding.userName
        passEditText = binding.myPass
        emailEditText = binding.userEmail
    }

    private fun playAnim() {
        ObjectAnimator.ofFloat(binding.welcome, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val subTitle = ObjectAnimator.ofFloat(binding.subTitle, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.nameLay, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailLay, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.passLay, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(subTitle, name, email, pass, signup)
            startDelay = 600
        }.start()
    }

    private fun setupAction() {
        binding.signUp.setOnClickListener {
            val name = binding.userName.text.toString()
            val email = binding.userEmail.text.toString()
            val password = binding.myPass.text.toString()
            viewModel.postRegister(name, email, password)
            viewModel.regResp.observe(this) { resp ->
                if (!resp.error) {
                    val intentToLogin = Intent(this, SignInActivity::class.java)
                    startActivity(intentToLogin)
                    finish()
                }
            }
        }
    }

    private fun showLoad(isLoad: Boolean) {
        binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.INVISIBLE
    }
}