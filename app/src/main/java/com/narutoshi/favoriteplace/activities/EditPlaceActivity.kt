package com.narutoshi.favoriteplace.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.narutoshi.favoriteplace.*
import com.narutoshi.favoriteplace.models.FavoritePlaceModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_edit_place.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class EditPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var mode: String? = null

    private var title: String? = null
    private var description: String? = null
    private var date: String? = null
    private var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_place)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        mode = intent.getStringExtra(IntentKey.MODE_IN_EDIT)

        if (mode == ModeOfEdit.NEW_ENTRY) {
            setDefaultDate()
        }

        if (mode == ModeOfEdit.EDIT) {
            title = intent.getStringExtra(IntentKey.TITLE)
            description = intent.getStringExtra(IntentKey.DESCRIPTION)
            date = intent.getStringExtra(IntentKey.DATE)

            val passedImageString = intent.getStringExtra(IntentKey.IMAGE_STRING)
            if(passedImageString != DefaultImage.STRING) {
                imageURI = Uri.parse(passedImageString)
                iv_place.setImageURI(imageURI)
            } else {
                iv_place.setBackgroundResource(DefaultImage.RESOURCE)
            }

            et_title.setText(title)
            et_description.setText(description)
            et_date.setText(date)

        }

        et_date.setOnClickListener(this)
        iv_place.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.apply {
            findItem(R.id.action_delete).isVisible = false
            findItem(R.id.action_edit).isVisible = false
            findItem(R.id.action_register).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_register) {
            recordToRealmDB(mode)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCode.GALLERY_REQUEST_CODE -> {
                    if (data != null) {
                        val contentURI = data.data
                        try {
                            @Suppress("DEPRECATION")
                            val selectedImageBitmap =
                                MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                            imageURI = saveImageToInternalStorage(selectedImageBitmap)

                            iv_place.setImageBitmap(selectedImageBitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(this, getString(R.string.gallery_fail), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                RequestCode.CAMERA_REQUEST_CODE -> {
                    val takenPhotoBitmap: Bitmap = data!!.extras!!.get("data") as Bitmap

                    imageURI = saveImageToInternalStorage(takenPhotoBitmap)

                    iv_place.setImageBitmap(takenPhotoBitmap)
                }
            }
        }
    }

    private fun recordToRealmDB(mode: String?) {
        if (!isTitleFilled()) {
            return
        }

        when (mode) {
            ModeOfEdit.NEW_ENTRY -> {
                addNewFavoritePlace()
            }

            ModeOfEdit.EDIT -> {
                updateExistingPlace()
            }
        }


    }

    private fun isTitleFilled(): Boolean {
        val userInputTitle = et_title.text.toString()

        if (userInputTitle.isBlank()) {
            til_title.error = getString(R.string.title_is_required)
            return false
        }

        return true
    }

    private fun addNewFavoritePlace() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val newFavoritePlace = realm.createObject(FavoritePlaceModel::class.java)
        newFavoritePlace.apply {
            title = et_title.text.toString()
            description = et_description.text.toString()
            date = et_date.text.toString()
            imageString = if(imageURI != null) imageURI.toString() else DefaultImage.STRING
        }

        realm.commitTransaction()
        realm.close()

        setResult(Activity.RESULT_OK)
        finish() // MainActivityへ返す
    }

    private fun updateExistingPlace() {
        val newTitle = et_title.text.toString()
        val newDescription = et_description.text.toString()
        val newDate = et_date.text.toString()

        Log.d("EditPlace", "$imageURI")

        val newImageString = if(imageURI != null) imageURI.toString() else DefaultImage.STRING

        val realm = Realm.getDefaultInstance()
        val selectedPlace = realm.where(FavoritePlaceModel::class.java)
            .equalTo(FavoritePlaceModel::title.name, title)
            .equalTo(FavoritePlaceModel::description.name, description)
            .equalTo(FavoritePlaceModel::date.name, date)
            .findFirst()
        realm.beginTransaction()
        selectedPlace!!.apply {
            title = newTitle
            description = newDescription
            date = newDate
            imageString = newImageString
        }
        realm.commitTransaction()
        realm.close()

        val intent = Intent(this, PlaceDetailActivity::class.java)
        intent.apply {
            putExtra(IntentKey.TITLE, newTitle)
            putExtra(IntentKey.DESCRIPTION, newDescription)
            putExtra(IntentKey.DATE, newDate)
            putExtra(IntentKey.IMAGE_STRING, newImageString)
        }

        Log.d("EditPlace", "$newImageString") // DEFAULT_IMAGE

        setResult(Activity.RESULT_OK, intent)
        finish() // PlaceDetailActivityに戻る。アップデートされた内容も共に。
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDefaultDate() {
        val today = SimpleDateFormat("yyyy/MM/dd").format(Date())
        et_date.setText(today)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.et_date -> onDateSet()

            R.id.iv_place -> {
                onImgViewClicked()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun onDateSet() {
        val calender = Calendar.getInstance()
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calender.set(Calendar.YEAR, year)
                calender.set(Calendar.MONTH, month)
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val selectedDate = SimpleDateFormat("yyyy/MM/dd").format(calender.time)
                et_date.setText(selectedDate)
            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun onImgViewClicked() {
        val dialogItems = arrayOf(getString(R.string.select_photo_from_gallery), getString(R.string.capture_photo_from_camera))

        AlertDialog.Builder(this).apply {
            setTitle("Select Action")
            setItems(dialogItems) { _, which ->
                when (which) {
                    0 -> {
                        choosePhotoFromGallery()
                    }

                    1 -> {
                        takePhotoFromCamera()
                    }
                }
            }
        }.show()
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this@EditPlaceActivity).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(
                            intent,
                            RequestCode.GALLERY_REQUEST_CODE
                        )
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()

                }
            }).onSameThread()
            .check()
    }

    private fun takePhotoFromCamera() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, RequestCode.CAMERA_REQUEST_CODE)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.rational_dialog_message))
            .setPositiveButton(
                getString(R.string.go_to_settings)
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }
}
