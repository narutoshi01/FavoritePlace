package com.narutoshi.favoriteplace.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.narutoshi.favoriteplace.IntentKey
import com.narutoshi.favoriteplace.R
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

    
}
