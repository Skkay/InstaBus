package fr.skkay.instabus.dataclass

data class StationItem(
    val id: String,
    val streetName: String,
    val city: String,
    val utm_x: String,
    val utm_y: String,
    val lat: String,
    val lon: String,
    val furniture: String,
    val buses: String
)
