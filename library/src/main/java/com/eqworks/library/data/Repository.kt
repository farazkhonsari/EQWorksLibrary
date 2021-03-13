package com.eqworks.library.data

import com.eqworks.library.models.LocationEvent



class Repository(private val remoteDataSource: DataSource) {
    suspend fun log(event: LocationEvent) {
        remoteDataSource.log(event)
    }
}