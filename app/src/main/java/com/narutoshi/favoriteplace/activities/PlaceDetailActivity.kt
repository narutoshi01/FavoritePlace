package com.narutoshi.favoriteplace.activities

import android.app.Activity
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.narutoshi.favoriteplace.IntentKey
import com.narutoshi.favoriteplace.R
import com.narutoshi.favoriteplace.models.FavoritePlaceModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_place_detail.*

class PlaceDetailActivity : AppCompatActivity() {

    private var title: String? = null
    private var description: String? = null
    private var date: String? = null
    private var imageURI: String? = null

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
        imageURI = intent.getStringExtra(IntentKey.IMAGE_URI)

        tv_title.text = title
        tv_description.text = description
        tv_date.text = date
        // TODO image view に取得したURIを使って画像をセットする
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
        when(item.itemId) {
            R.id.action_delete -> {
                onDeleteBtnClicked()
            }

            R.id.action_edit -> {
                // TODO EditActivityへ。modeはEditで。インテントで情報渡す
                Toast.makeText(this, "action edit", Toast.LENGTH_SHORT).show()

            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onDeleteBtnClicked() {
        AlertDialog.Builder(this).apply {
            setTitle("DELETE")
            setMessage("are you sure to delete?")
            setPositiveButton("Yes") {dialog, which ->
                deleteFromRealm()
            }
            setNegativeButton("No") {dialog, which ->  }
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
}

