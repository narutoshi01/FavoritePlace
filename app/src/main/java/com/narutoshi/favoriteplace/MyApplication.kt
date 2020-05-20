package com.narutoshi.favoriteplace

import android.app.Application
import android.content.Context
import io.realm.Realm

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        appContext = this
    }

    companion object {
        lateinit var appContext: Context
    }
}