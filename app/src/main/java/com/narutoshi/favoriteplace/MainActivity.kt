package com.narutoshi.favoriteplace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val intent = Intent(this, EditPlaceActivity::class.java)
            startActivityForResult(intent, RequestCode.EDIT_PLACE_ACTIVITY_REQUEST_CODE)
        }

    }
}
