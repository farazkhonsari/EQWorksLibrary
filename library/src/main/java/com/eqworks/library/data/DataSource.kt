package com.eqworks.library.data
import com.eqworks.library.models.LocationEvent

import retrofit2.http.Body
import retrofit2.http.POST


interface DataSource {
    @POST("./log")
    suspend fun log(@Body event: LocationEvent)
}