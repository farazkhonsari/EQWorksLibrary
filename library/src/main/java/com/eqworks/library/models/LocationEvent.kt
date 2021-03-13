package com.eqworks.library.models

data class LocationEvent(
    val lat: Float,
    val lon: Float,
    val ext: String = "",
    val time: Long = System.currentTimeMillis() / 1000,
    var accuracy: Accuracy? = null
)

enum class Accuracy {
    Accurate,
    Estimate,
    None,
}