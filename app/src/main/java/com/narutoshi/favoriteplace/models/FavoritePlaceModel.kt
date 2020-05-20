package com.narutoshi.favoriteplace.models

import io.realm.RealmObject

open class FavoritePlaceModel: RealmObject() {
    var title: String = ""
    var description: String = ""
    var date:String = ""
    var image: String = "" // URI
}