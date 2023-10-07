package com.prashant.smd.ui.admin

import com.prashant.smd.adapter.ImageAdapter
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.nguyenhoanglam.imagepicker.model.CustomMessage
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import com.prashant.smd.databinding.ActivityAddHomeBinding
import com.prashant.smd.firebase.Constant
import com.prashant.smd.model.ImageHomeModel
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

class AddHomeActivity : AppCompatActivity() {
    private lateinit var bind: ActivityAddHomeBinding
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val databaseReference: DatabaseReference = Constant.homeDB

    private var adapter: ImageAdapter? = null
    private var images = ArrayList<Image>()
    private var selectedOption: String? = null

    private val launcher = registerImagePicker {
        images = it
        adapter!!.setData(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAddHomeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.backBtn.setOnClickListener {
            finish()
        }

        bind.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            selectedOption = selectedRadioButton.text.toString()
        }

        bind.uploadPhotoBtn.setOnClickListener {
            if (selectedOption == null || selectedOption!!.isEmpty()) {
                Toast.makeText(this, "Select any category!", Toast.LENGTH_SHORT).show()
            } else if (images.isEmpty()) {
                Toast.makeText(this, "Select at least 1 image!", Toast.LENGTH_SHORT).show()
            } else {

                uploadImages(images) {
                    Log.d("LIST", "onCreate: $it.toString()")
                    uiThreadToast("Images uploaded successfully!")
                }
            }
        }

        adapter = ImageAdapter(this)
        val recyclerView = bind.selectedImageRecyclerview
        val layoutManager =
            GridLayoutManager(this@AddHomeActivity, 2) // Change 3 to the number of columns you want
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val config = ImagePickerConfig(
            limitSize = 10,
            customMessage = CustomMessage(
                reachLimitSize = "You can only select up to 10 images.",
                cameraError = "Unable to open camera.",
                noCamera = "Your device has no camera.",
                noImage = "No image found.",
                noPhotoAccessPermission = "Please allow permission to access photos and media.",
                noCameraPermission = "Please allow permission to access camera."
            )
        )

        val pickImagesButton = bind.addImageBtn
        pickImagesButton.setOnClickListener {
            launcher.launch(config)
        }
    }

    private fun uploadImages(images: List<Image>, onUploadComplete: (List<String>) -> Unit) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading Images...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val compressedImageUrls = mutableListOf<String>()
        val executor = Executors.newFixedThreadPool(images.size)

        for (image in images) {
            executor.execute {
                try {
                    // Generate a unique name for the image (you can customize this)
                    val uniqueImageName = "${System.currentTimeMillis()}_${image.name}"

                    // Compress the image (you can adjust the quality and size as needed)
                    val compressedImage = compressImage(image.uri)

                    // Create a reference to the Firebase Storage location
                    val imageRef = storageReference.child("images/$uniqueImageName")

                    // Upload the compressed image to Firebase Storage
                    val uploadTask: UploadTask = imageRef.putBytes(compressedImage!!)

                    // Monitor the upload process
                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
                        downloadUrl?.addOnSuccessListener { uri ->
                            // Add the download URL to the list of compressed image URLs
                            compressedImageUrls.add(uri.toString())

                            if (compressedImageUrls.size == images.size) {
                                // All images are uploaded, call the callback function
                                progressDialog.dismiss()
                                onUploadComplete(compressedImageUrls)

                                val imageDataList = mutableListOf<ImageHomeModel>()

                                for ((index, imageUrl) in compressedImageUrls.withIndex()) {
                                    val imageData = ImageHomeModel()
                                    imageData.imageUrl = imageUrl
                                    imageData.imageName = getImageNameFromUrl(imageUrl.toString()) // Use the same unique name for Realtime DB
                                    imageData.uid = databaseReference.push().key.toString()
                                    imageData.category = selectedOption.toString()
                                    imageDataList.add(imageData)
                                }

                                for (imageData in imageDataList) {
                                    databaseReference.child(imageData.uid).setValue(imageData)
                                }
                            }
                        }
                    }.addOnFailureListener { e ->
                        progressDialog.dismiss()
                        uiThreadToast(e.message)
                    }
                } catch (e: Exception) {
                    progressDialog.dismiss()
                    uiThreadToast(e.message)
                }
            }
        }
    }

    private fun getImageNameFromUrl(storageUrl: String): String {
        val regex = Regex("images%2F(.*?)(\\?alt.*)")
        val matchResult = regex.find(storageUrl)
        return matchResult?.groups?.get(1)?.value.toString()
    }

    private fun uiThreadToast(message: String?) {
        runOnUiThread {
            Toast.makeText(this@AddHomeActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun compressImage(imageUri: Uri): ByteArray? {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            // First, decode the image to get its dimensions
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // Calculate the desired dimensions to fit under 300KB
            val targetSize = 3048 * 1024 // 3MB in bytes
            val imageWidth = options.outWidth
            val imageHeight = options.outHeight
            val sampleSize = calculateSampleSize(imageWidth, imageHeight, targetSize)

            // Now, decode the image with the calculated sample size and compress it
            val newInputStream = contentResolver.openInputStream(imageUri)
            options.inJustDecodeBounds = false
            options.inSampleSize = sampleSize

            val bitmap = BitmapFactory.decodeStream(newInputStream, null, options)
            newInputStream?.close()

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                byteArrayOutputStream
            ) // Adjust compression quality as needed
            return byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun calculateSampleSize(width: Int, height: Int, targetSize: Int): Int {
        val imageSize = width * height * 4 // Assuming 4 bytes per pixel (32-bit)
        var sampleSize = 1
        while (imageSize / (sampleSize * sampleSize) > targetSize) {
            sampleSize *= 2
        }
        return sampleSize
    }
}