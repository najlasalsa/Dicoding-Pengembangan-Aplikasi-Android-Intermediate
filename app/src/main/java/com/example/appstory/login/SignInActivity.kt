package com.example.appstory.login

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
import com.example.appstory.data.UserModel
import com.example.appstory.data.UserPreference
import com.example.appstory.data.ViewModelFactory
import com.example.appstory.customviews.EmailEditText
import com.example.appstory.customviews.PassEditText
import com.example.appstory.databinding.ActivitySignInBinding
import com.example.appstory.main.MainActivity
import com.example.appstory.register.SignUpActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var viewModel: SignInViewModel
    private lateinit var emailEditText: EmailEditText
    private lateinit var passEditText: PassEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
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
        )[SignInViewModel::class.java]

        viewModel.isLoad.observe(this) {
            showLoad(it)
        }

        viewModel.message.observe(this) { response ->
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }

    private fun customView() {
        emailEditText = binding.userEmail
        passEditText = binding.myPass
    }

    private fun playAnim() {
        ObjectAnimator.ofFloat(binding.welcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.title, View.ALPHA, 1f).setDuration(500)
        val subTitle = ObjectAnimator.ofFloat(binding.subTitle, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailLay, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.passLay, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.login, View.ALPHA, 1f).setDuration(500)
        val acc = ObjectAnimator.ofFloat(binding.noAcc, View.ALPHA, 1f).setDuration(500)
        val create = ObjectAnimator.ofFloat(binding.createAcc, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, subTitle, email, pass, login, acc, create)
            startDelay = 600
        }.start()
    }

    private fun setupAction() {
        binding.login.setOnClickListener {
            val email = binding.userEmail.text.toString()
            val password = binding.myPass.text.toString()
            viewModel.postLogin(email, password)
            viewModel.loginResp.observe(this) { resp ->
                saveUserSession(
                    UserModel(
                        resp.loginResult.token,
                        true
                    )
                )
            }
        }
        binding.createAcc.setOnClickListener {
            val intentToSignUp = Intent(this, SignUpActivity::class.java)
            startActivity(intentToSignUp)
            finish()
        }
    }

    private fun saveUserSession(user: UserModel) {
        viewModel.saveUser(user)
        viewModel.userLogin()
        val intentToMain = Intent(this, MainActivity::class.java)
        startActivity(intentToMain)
        finish()
    }

    private fun showLoad(isLoad: Boolean) {
        binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.INVISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}