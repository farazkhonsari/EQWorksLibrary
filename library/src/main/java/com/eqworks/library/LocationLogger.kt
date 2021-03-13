package com.eqworks.library

import com.eqworks.library.data.Repository
import com.eqworks.library.models.Accuracy
import com.eqworks.library.models.DeviceLocationInfo
import com.eqworks.library.models.LocationEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


internal class LocationLogger(var repository: Repository) {
    suspend fun log(
        event: LocationEvent,
        deviceLocationInfo: DeviceLocationInfo
    ): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                if (deviceLocationInfo.isCoarseLocationAccessGranted || deviceLocationInfo.isFineLocationAccessGranted) {
                    if (event.accuracy == null) {
                        event.accuracy = getDeviceMostPossibleAccuracy(deviceLocationInfo)
                    }
                    repository.log(event)
                } else {
                    repository.log(event.copy(lat = 0F, lon = 0F, accuracy = Accuracy.None))
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun getDeviceMostPossibleAccuracy(
        deviceLocationInfo: DeviceLocationInfo
    ): Accuracy = if (!deviceLocationInfo.gpsEnabled && !deviceLocationInfo.networkEnabled) {
        Accuracy.None
    } else if (deviceLocationInfo.gpsEnabled && deviceLocationInfo.isFineLocationAccessGranted) {
        Accuracy.Accurate
    } else {
        Accuracy.Estimate
    }


}