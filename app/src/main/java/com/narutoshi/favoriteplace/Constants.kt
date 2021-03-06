package com.narutoshi.favoriteplace

object RequestCode {
    const val EDIT_PLACE_ACTIVITY_REQUEST_CODE = 1
    const val PLACE_DETAIL_ACTIVITY_REQUEST_CODE = 2
    const val GALLERY_REQUEST_CODE = 3
    const val CAMERA_REQUEST_CODE = 4
}

object ModeOfEdit {
    const val NEW_ENTRY = "NEW_ENTRY"
    const val EDIT = "EDIT"
}

object IntentKey {
    const val MODE_IN_EDIT = "MODE_IN_EDIT"
    const val TITLE = "TITLE"
    const val DESCRIPTION = "DESCRIPTION"
    const val DATE = "DATE"
    const val IMAGE_STRING = "IMAGE_STRING"
}

const val IMAGE_DIRECTORY = "FavoritePlaceImages"

object DefaultImage {
    const val STRING = "DEFAULT_IMAGE"
    const val RESOURCE = R.drawable.image_placeholder
}

