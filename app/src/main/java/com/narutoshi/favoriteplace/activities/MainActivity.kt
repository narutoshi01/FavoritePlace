package com.narutoshi.favoriteplace.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.narutoshi.favoriteplace.IntentKey
import com.narutoshi.favoriteplace.ModeOfEdit
import com.narutoshi.favoriteplace.R
import com.narutoshi.favoriteplace.RequestCode
import com.narutoshi.favoriteplace.adapters.FavoritePlacesAdapter
import com.narutoshi.favoriteplace.models.FavoritePlaceModel
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val intent = Intent(this, EditPlaceActivity::class.java)
            intent.putExtra(
                IntentKey.MODE_IN_EDIT,
                ModeOfEdit.NEW_ENTRY
            )
            startActivityForResult(intent,
                RequestCode.EDIT_PLACE_ACTIVITY_REQUEST_CODE
            )
        }

        getFavoritePlacesListFromDB()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                RequestCode.EDIT_PLACE_ACTIVITY_REQUEST_CODE -> {
                    getFavoritePlacesListFromDB()
                }

                RequestCode.PLACE_DETAIL_ACTIVITY_REQUEST_CODE -> {
                    getFavoritePlacesListFromDB()
                }
            }
        }
    }

    private fun getFavoritePlacesListFromDB() {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(FavoritePlaceModel::class.java)
            .findAll()

        if (results.size > 0) {
            rv_favorite_places_list.visibility = View.VISIBLE
            tv_no_records.visibility = View.GONE

            setFavoritePlacesRecyclerView(results)

        } else {
            rv_favorite_places_list.visibility = View.GONE
            tv_no_records.visibility = View.VISIBLE
        }
    }

    private fun setFavoritePlacesRecyclerView(results: RealmResults<FavoritePlaceModel>) {
        rv_favorite_places_list.layoutManager = LinearLayoutManager(this)

        val placeAdapter = FavoritePlacesAdapter(this, results)
        rv_favorite_places_list.adapter = placeAdapter

        placeAdapter.setOnClickListener(object : FavoritePlacesAdapter.OnClickListener{
            override fun onCLick(model: FavoritePlaceModel) {
                // TODO DetailActivityへ行く
                val intent = Intent(this@MainActivity, PlaceDetailActivity::class.java)
                intent.apply {
                    putExtra(IntentKey.TITLE, model.title)
                    putExtra(IntentKey.DESCRIPTION, model.description)
                    putExtra(IntentKey.DATE, model.date)
                    putExtra(IntentKey.IMAGE_URI, model.imageURI)
                }
                startActivityForResult(intent, RequestCode.PLACE_DETAIL_ACTIVITY_REQUEST_CODE)
            }
        })


    }
}
