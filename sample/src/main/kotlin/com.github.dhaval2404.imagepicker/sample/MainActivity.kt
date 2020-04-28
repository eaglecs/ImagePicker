package com.github.dhaval2404.imagepicker.sample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.sample.util.FileUtil
import com.github.dhaval2404.imagepicker.sample.util.IntentUtil
import com.github.dhaval2404.imagepicker.util.IntentUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_camera_only.*
import kotlinx.android.synthetic.main.content_gallery_only.*
import kotlinx.android.synthetic.main.content_profile.*

class MainActivity : AppCompatActivity() {

    companion object {

        private const val GITHUB_REPOSITORY = "https://github.com/Dhaval2404/ImagePicker"

        private const val PROFILE_IMAGE_REQ_CODE = 101
        private const val GALLERY_IMAGE_REQ_CODE = 102
        private const val CAMERA_IMAGE_REQ_CODE = 103
    }

    private var mCameraUri: Uri? = null
    private var mGalleryUri: Uri? = null
    private var mProfileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        imgProfile.setDrawableImage(R.drawable.ic_person, true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_github -> {
                IntentUtil.openURL(this, GITHUB_REPOSITORY)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun pickProfileImage(view: View) {
        ImagePicker.with(this)
            // Crop Square image
           // .cropSquare()
            .setImageProviderInterceptor { imageProvider -> // Intercept ImageProvider
                Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
            }
            // Image resolution will be less than 512 x 512
            //.maxResultSize(512, 512)
            //.saveDir(getExternalFilesDir(null)!!)
            .start(PROFILE_IMAGE_REQ_CODE)
    }

    fun pickGalleryImage(view: View) {
        ImagePicker.with(this)
            // Crop Image(User can choose Aspect Ratio)
            .crop()
            // User can only select image from Gallery
            .galleryOnly()

            .galleryMimeTypes(  //no gif images at all
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            // Image resolution will be less than 1080 x 1920
            //.maxResultSize(360, 420)
            //.saveDir(getExternalFilesDir(null)!!)
            .start(GALLERY_IMAGE_REQ_CODE)
    }

    fun pickCameraImage(view: View) {
        ImagePicker.with(this)
            // User can only capture image from Camera
            .cameraOnly()
            // Image size will be less than 1024 KB
            .compress(50)
            .saveDir(getExternalFilesDir(null)!!)
            .start(CAMERA_IMAGE_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.e("TAG", "Path:${ImagePicker.getFilePath(data)}")
            // Uri object will not be null for RESULT_OK
            val uri = data?.data!!
            when (requestCode) {
                PROFILE_IMAGE_REQ_CODE -> {
                    mProfileUri = uri
                    imgProfile.setLocalImage(uri, true)
                }
                GALLERY_IMAGE_REQ_CODE -> {
                    mGalleryUri = uri
                    imgGallery.setLocalImage(uri)
                }
                CAMERA_IMAGE_REQ_CODE -> {
                    mCameraUri = uri
                    imgCamera.setLocalImage(uri)
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    fun showImageCode(view: View) {
        val resource = when (view) {
            imgProfileCode -> R.drawable.img_profile_code
            imgCameraCode -> R.drawable.img_camera_code
            imgGalleryCode -> R.drawable.img_gallery_code
            else -> 0
        }
        ImageViewerDialog.newInstance(resource).show(supportFragmentManager, "")
    }

    fun showImage(view: View) {
        val uri = when (view) {
            imgProfile -> mProfileUri
            imgCamera -> mCameraUri
            imgGallery -> mGalleryUri
            else -> null
        }

        uri?.let {
            startActivity(IntentUtils.getUriViewIntent(this, uri))
        }
    }

    fun showImageInfo(view: View) {
        val uri = when (view) {
            imgProfileInfo -> mProfileUri
            imgCameraInfo -> mCameraUri
            imgGalleryInfo -> mGalleryUri
            else -> null
        }

        AlertDialog.Builder(this)
            .setTitle("Image Info")
            .setMessage(FileUtil.getFileInfo(this, uri))
            .setPositiveButton("Ok", null)
            .show()
    }
}
