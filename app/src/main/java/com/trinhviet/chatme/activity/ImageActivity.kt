package com.trinhviet.chatme.activity

import android.Manifest
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.squareup.picasso.Picasso
import com.trinhviet.chatme.R
import com.trinhviet.chatme.databinding.ActivityImageBinding
import java.io.File


class ImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityImageBinding


    var permissions = arrayOf<String>(
        Manifest.permission.INTERNET,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 2.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image)

        var imageUrl = intent.getStringExtra("image")

        Picasso.get().load(imageUrl).into(binding.image)

        checkPermissions()

        binding.download.setOnClickListener {
            try {
                val dm: DownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                val downLoadUri = Uri.parse(imageUrl)
                val request = DownloadManager.Request(downLoadUri)

                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle((System.currentTimeMillis()).toString())
                    .setMimeType("image/jpeg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_PICTURES,
                        File.separator + (System.currentTimeMillis()).toString() + ".jpg"
                    )
                dm.enqueue(request)

                Toast.makeText(this@ImageActivity, "Downloading...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ImageActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
    }

    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        scaleGestureDetector!!.onTouchEvent(motionEvent)
        return true
    }

    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), 100)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "permission", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }

    inner class ScaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector ): Boolean {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = 0.1f.coerceAtLeast(mScaleFactor.coerceAtMost(10.0f));
            binding.image.scaleX = mScaleFactor;
            binding.image.scaleY = mScaleFactor;
            return true;
        }
    }
}
