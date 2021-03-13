package com.eqworks.library.models

internal data class DeviceLocationInfo(
        var gpsEnabled: Boolean,
        var networkEnabled: Boolean,
        var isFineLocationAccessGranted: Boolean,
        var isCoarseLocationAccessGranted: Boolean
)
