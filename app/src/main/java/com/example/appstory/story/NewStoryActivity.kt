package com.example.appstory.story

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.appstory.*
import com.example.appstory.data.UserPreference
import com.example.appstory.data.ViewModelFactory
import com.example.appstory.databinding.ActivityNewStoryBinding
import com.example.appstory.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var viewModel: NewStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.add_story)

        setupView()
        setupViewModel()
        action()
        permit()
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

    private fun action() {
        binding.apply {
            binding.cameraButt.setOnClickListener { takePhoto() }
            binding.galleryButt.setOnClickListener { gallery() }
            binding.uploadButt.setOnClickListener { uploadPhoto() }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(UserPreference.getInstance(dataStore))
        )[NewStoryViewModel::class.java]

        viewModel.isLoad.observe(this) {
            showLoad(it)
        }

        viewModel.message.observe(this) { response ->
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }

    private fun permit() {
        if (!allPermitGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMIT, REQUEST_CODE
            )
        }
    }

    private fun allPermitGranted() = REQUIRED_PERMIT.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = rotateBitmap(BitmapFactory.decodeFile(getFile?.path), true)
            binding.camPrev.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.camPrev.setImageURI(selectedImg)
        }
    }

    private fun gallery() {
        val i = Intent()
        i.action = Intent.ACTION_GET_CONTENT
        i.type = "image/*"
        val chooser = Intent.createChooser(i, getString(R.string.choose_pic))
        launcherIntentGallery.launch(chooser)
    }

    private fun takePhoto() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        i.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val pictURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.appstory",
                it
            )

            currentPhotoPath = it.absolutePath
            i.putExtra(MediaStore.EXTRA_OUTPUT, pictURI)
            launcherIntentCamera.launch(i)
        }
    }

    private fun uploadPhoto() {
        viewModel.getUser().observe(this) {
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)
                val desc =
                    binding.userDesc.text.toString().toRequestBody("text/plain".toMediaType())
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestFile
                )
                uploadResp(it.token, imageMultipart, desc)
            } else {
                Toast.makeText(this, getString(R.string.pict), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadResp(token: String, file: MultipartBody.Part, description: RequestBody) {
        viewModel.addStories(token, file, description)
        viewModel.addResp.observe(this) {
            if (!it.error) {
                val intentToMain = Intent(this, MainActivity::class.java)
                intentToMain.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentToMain)
                finish()
            }
        }
    }

    private fun showLoad(isLoad: Boolean) {
        binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        private val REQUIRED_PERMIT = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE = 10
    }
}