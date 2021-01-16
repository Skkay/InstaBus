package fr.skkay.instabus.dataclass

import java.util.Date

data class PhotoItem (
    val title: String,
    val image: String,
    val datetime: Date,
    val station_id: String
)
