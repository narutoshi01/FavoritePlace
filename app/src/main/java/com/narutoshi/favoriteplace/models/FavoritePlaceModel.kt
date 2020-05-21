package com.narutoshi.favoriteplace.models

import io.realm.RealmObject
import java.io.Serializable

open class FavoritePlaceModel : RealmObject() {
    var title: String = ""
    var description: String = ""
    var date: String = ""
    var imageString: String = "" // URI format
}