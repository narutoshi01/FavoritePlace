package com.narutoshi.favoriteplace.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.narutoshi.favoriteplace.*
import com.narutoshi.favoriteplace.models.FavoritePlaceModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_place_detail.*

class PlaceDetailActivity : AppCompatActivity() {

    private var title: String? = null
    private var description: String? = null
    private var date: String? = null

    private var favoritePlaceModel: FavoritePlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        title = intent.getStringExtra(IntentKey.TITLE)
        description = intent.getStringExtra(IntentKey.DESCRIPTION)
        date = intent.getStringExtra(IntentKey.DATE)


        favoritePlaceModel = getPlaceItemFromDB(title, description, date)

        setViewItems()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.apply {
            findItem(R.id.action_delete).isVisible = true
            findItem(R.id.action_edit).isVisible = true
            findItem(R.id.action_register).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                onDeleteBtnClicked()
            }

            R.id.action_edit -> {
                goToEditScreen()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCode.EDIT_PLACE_ACTIVITY_REQUEST_CODE -> {
                    title = data?.getStringExtra(IntentKey.TITLE)
                    description = data?.getStringExtra(IntentKey.DESCRIPTION)
                    date = data?.getStringExtra(IntentKey.DATE)

                    favoritePlaceModel = getPlaceItemFromDB(title, description, date)

                    setViewItems()
                }
            }
        }
    }

    private fun onDeleteBtnClicked() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.dialog_delete_title))
            setMessage(getString(R.string.dialog_delete_message))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteFromRealm()
            }
            setNegativeButton(getString(R.string.no)) { _, _ -> }
        }.show()
    }

    private fun deleteFromRealm() {
        val realm = Realm.getDefaultInstance()
        val selectedPlace = realm.where(FavoritePlaceModel::class.java)
            .equalTo(FavoritePlaceModel::title.name, title)
            .equalTo(FavoritePlaceModel::description.name, description)
            .equalTo(FavoritePlaceModel::date.name, date)
            .findFirst()
        realm.beginTransaction()
        selectedPlace?.deleteFromRealm()
        realm.commitTransaction()
        realm.close()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun goToEditScreen() {
        val intent = Intent(this, EditPlaceActivity::class.java)
        intent.apply {
            putExtra(IntentKey.TITLE, title)
            putExtra(IntentKey.DESCRIPTION, description)
            putExtra(IntentKey.DATE, date)
            putExtra(IntentKey.MODE_IN_EDIT, ModeOfEdit.EDIT)
        }

        if(favoritePlaceModel?.imageString != DefaultImage.STRING) {
            intent.putExtra(IntentKey.IMAGE_STRING, favoritePlaceModel?.imageString)
        } else {
            intent.putExtra(IntentKey.IMAGE_STRING, DefaultImage.STRING)
        }

        startActivityForResult(intent, RequestCode.EDIT_PLACE_ACTIVITY_REQUEST_CODE)
    }

    private fun setViewItems() {
        tv_title.text = favoritePlaceModel?.title
        tv_description.text = favoritePlaceModel?.description
        tv_date.text = favoritePlaceModel?.date

        if(favoritePlaceModel?.imageString != DefaultImage.STRING) {
            iv_place.setImageURI(Uri.parse(favoritePlaceModel?.imageString))
        } else {
            iv_place.setBackgroundResource(DefaultImage.RESOURCE)
        }

    }

    private fun getPlaceItemFromDB(title: String?, description: String?, date: String?) : FavoritePlaceModel? {
        val realm = Realm.getDefaultInstance()
        val selectedPlaceModel = realm.where(FavoritePlaceModel::class.java)
            .equalTo(FavoritePlaceModel::title.name, title)
            .equalTo(FavoritePlaceModel::description.name, description)
            .equalTo(FavoritePlaceModel::date.name, date)
            .findFirst()

        return selectedPlaceModel
    }
}

