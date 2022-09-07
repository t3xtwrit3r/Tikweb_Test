package com.mubin.tikweb_test.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.firebase.storage.FirebaseStorage
import com.mubin.tikweb_test.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListener()

    }

    private fun initClickListener() {
        binding.btnSelectImages.setOnClickListener {

            if (Build.VERSION.SDK_INT >= 23 ) {
                if (isCheckPermission()){
                    chooseImageToUpload()
                }
            } else {
                isCheckPermissionBelow23()
            }

        }

        binding.btnViewImages.setOnClickListener {
            getImages()
        }
    }

    private fun chooseImageToUpload() {
        ImagePicker.create(this)
            .folderMode(true)
            .showCamera(true)
            .toolbarFolderTitle("Select Folder")
            .toolbarImageTitle("Select Images")
            .includeVideo(false)
            .limit(40)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            val imageList = ImagePicker.getImages(data)
            val pathList = imageList.map { it.uri }
            val filterImageList: MutableList<Image> = mutableListOf()

            pathList.forEachIndexed { index, _ ->
                filterImageList.add(imageList[index])
            }

            binding.tvImageSelectedCount.text = "${filterImageList.size} images selected for upload"
            uploadImagesToFirebaseStorage(filterImageList)

        }

        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun uploadImagesToFirebaseStorage(filterImageList: MutableList<Image>) {

        val imagefolder = FirebaseStorage.getInstance().reference.child("Image_Folder")

        binding.progressBar.visibility = View.VISIBLE
        
        for (item in filterImageList) {

            val individualImage: Uri = item.uri

            val imageName = imagefolder.child("Image" + individualImage.lastPathSegment)

            Log.d("ImageToBeUpload", "$imageName")

            imageName.putFile(individualImage).addOnCompleteListener {
                 if (it.isComplete){
                     binding.progressBar.visibility = View.GONE
                     Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
                     binding.tvImageSelectedCount.text = ""
                 } else {
                     binding.progressBar.visibility = View.GONE
                     Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
                     binding.tvImageSelectedCount.text = ""
                 }
            }

        }

    }

    private fun getImages() {

        val databaseReference = FirebaseStorage.getInstance().getReference("Image_Folder")

        databaseReference.parent?.child("Image_Folder")?.listAll()?.addOnCompleteListener { it ->
            if (it.isSuccessful) {

                val result = it.result

                val items = result.items

                for (i in items) {
                    Log.d("Images", "${i.downloadUrl}")
                }

            } else {

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

            }

        }

    }

    private fun isCheckPermissionBelow23() {
        val permissionRead = PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionWrite = PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionRead == PermissionChecker.PERMISSION_GRANTED && permissionWrite == PermissionChecker.PERMISSION_GRANTED) {

            chooseImageToUpload()

        } else {
            Toast.makeText(this, "Storage Permission is needed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isCheckPermission(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return when {
            permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED -> {
                true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                false
            }
            else -> {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                false
            }
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach { permission ->
            if (permission.key == Manifest.permission.READ_EXTERNAL_STORAGE && permission.key == Manifest.permission.WRITE_EXTERNAL_STORAGE) {

                chooseImageToUpload()

            }
        }
    }

}