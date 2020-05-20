package com.narutoshi.favoriteplace.activities

import android.app.Activity
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_place)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        mode = intent.getStringExtra(IntentKey.MODE_IN_EDIT)

        setDefaultDate()

        et_date.setOnClickListener(this)
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

    private fun isTitleFilled(): Boolean {
        val userInputTitle = et_title.text.toString()

        if (userInputTitle.isBlank()) {
            til_title.error = "Title is required"
            return false
        }

        return true
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
                // TODO アップデート
            }
        }

        setResult(Activity.RESULT_OK)
        finish() // ActivityForResult　で来ているので返す
    }

    private fun addNewFavoritePlace() {
        //  Todo 新規登録
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val newFavoritePlace = realm.createObject(FavoritePlaceModel::class.java)
        newFavoritePlace.apply {
            title = et_title.text.toString()
            description = et_description.text.toString()
            date = et_date.text.toString()
            image = "" // TODO need image URI here
        }

        realm.commitTransaction()
        realm.close()
    }

    private fun setDefaultDate() {
        val today = SimpleDateFormat("yyyy/MM/dd").format(Date())
        et_date.setText(today)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.et_date -> onDateSet()
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
}
