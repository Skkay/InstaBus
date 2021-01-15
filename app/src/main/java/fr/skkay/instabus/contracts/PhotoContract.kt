package fr.skkay.instabus.contracts

import android.provider.BaseColumns

class PhotoContract {
    object PhotoEntry : BaseColumns {
        const val TABLE_NAME = "photoList"
        const val _ID = "_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_STATION_ID = "stationId"
    }
}
