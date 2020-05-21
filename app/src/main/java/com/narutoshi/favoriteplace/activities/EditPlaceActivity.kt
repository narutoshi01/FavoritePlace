package com.narutoshi.favoriteplace.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.narutoshi.favoriteplace.models.FavoritePlaceModel
import com.narutoshi.favoriteplace.IntentKey
import com.narutoshi.favoriteplace.ModeOfEdit
import com.narutoshi.favoriteplace.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_edit_place.*
import java.text.SimpleDateFormat
import java.util.*

class EditPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var mode: String? = null

    private var title: String? = null
    private var description: String? = null
    private var date: String? = null
    private var imageURI: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_place)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        mode = intent.getStringExtra(IntentKey.MODE_IN_EDIT)

        if(mode == ModeOfEdit.NEW_ENTRY) {
            setDefaultDate()
        }

        if(mode == ModeOfEdit.EDIT) {
            title = intent.getStringExtra(IntentKey.TITLE)
            description = intent.getStringExtra(IntentKey.DESCRIPTION)
            date = intent.getStringExtra(IntentKey.DATE)
            imageURI = intent.getStringExtra(IntentKey.IMAGE_URI)

            et_title.setText(title)
            et_description.setText(description)
            et_date.setText(date)
            // TODO インテントで渡されたuriを使って画像をセット
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
            til_title.error = "Title is required"
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
            imageURI = "" // TODO need image URI here
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
        val newImageURI = "" // TODO URIをセットする

        val realm = Realm.getDefaultInstance()
        val selectedPlace = realm.where(FavoritePlaceModel::class.java)
            .equalTo(FavoritePlaceModel::title.name, title)
            .equalTo(FavoritePlaceModel::description.name, description)
            .equalTo(FavoritePlaceModel::date.name, date)
            .findFirst()
        realm.beginTransaction()
        selectedPlace?.apply {
            title = newTitle
            description = newDescription
            date = newDate
            imageURI = newImageURI
        }
        realm.commitTransaction()
        realm.close()

        val intent = Intent(this, PlaceDetailActivity::class.java)
        intent.apply {
            putExtra(IntentKey.TITLE, newTitle)
            putExtra(IntentKey.DESCRIPTION, newDescription)
            putExtra(IntentKey.DATE, newDate)
            putExtra(IntentKey.IMAGE_URI, newImageURI)
        }

        setResult(Activity.RESULT_OK, intent)
        finish() // PlaceDetailActivityに戻る。アップデートされた内容も共に。
    }

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

    private fun onDateSet() {
        val calender = Calendar.getInstance()
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
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
        val dialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")

        AlertDialog.Builder(this).apply {
            setTitle("Select Action")
            setItems(dialogItems) {dialog, which ->
                when(which) {
                    0 -> {
                        // choose photo from gallery
                        Toast.makeText(this@EditPlaceActivity, "Gallery", Toast.LENGTH_SHORT).show()
                    }

                    1 -> {
                        // take photo from camera
                        Toast.makeText(this@EditPlaceActivity, "Camera", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.show()
    }
}
