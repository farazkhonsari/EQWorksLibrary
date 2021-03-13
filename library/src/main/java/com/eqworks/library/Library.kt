package com.eqworks.library

import android.Manifest
import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.eqworks.library.di.ServiceLocator
import com.eqworks.library.models.DeviceLocationInfo
import com.eqworks.library.models.LocationEvent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object Library {
    private const val TAG: String = "Library"
    private var isInitialized: Boolean = false
    private var application: Application? = null
    private val locationLogger: LocationLogger
        get() = ServiceLocator.provideLocationLogger()

    /**
     * Configure the SDK
     * This may be called only once per application process lifetime.
     * @param application Your application object.
     */
    fun setup(application: Application): Boolean {
        if (isInitialized) {
            return true
        }
        Library.application = application
        isInitialized = true
        return true
    }

    /**
     * Log Location event
     * this can called when you want log an event.
     *
     * @param event Your Location Event object.
     * @param success call back that called when request is completed.
     * @param failed call back that called when request is failed.
     */
    fun log(
        event: LocationEvent,
        success: (() -> Unit)? = null,
        failed: (() -> Unit)? = null
    ) {
        GlobalScope.launch {
            try {
                var result: Boolean = false
                application?.let { application ->
                    result = locationLogger.log(
                        event = event,
                        deviceLocationInfo = getCurrentLocationDeviceInfo(application)
                    )
                }

                if (result) {
                    success?.invoke()
                } else {
                    failed?.invoke()
                }

            } catch (e: Exception) {

            }
        }
    }

    /**
     * Log Location event in suspend mode
     * this can called when you want log an event.
     * @param event Your Location Event object.
     * @return true if request send successful. return false if request failed or library not setup
     */
    suspend fun suspendLog(
        event: LocationEvent,
        success: (() -> Unit)? = null,
        failed: (() -> Unit)? = null
    ): Boolean {
        return withContext(Dispatchers.IO) {
            if (!isInitialized) {
                Log.e(
                    TAG,
                    "you should call setup function  before use library! you can setup it in application class"
                )
                return@withContext false

            } else {
                return@withContext try {

                    true
                } catch (e: Exception) {

                    false
                }
            }
        }


    }

    private fun getCurrentLocationDeviceInfo(application: Application): DeviceLocationInfo {
        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        val isFineLocationAccessGranted =
            ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED;
        val isCoarseLocationAccessGranted =
            ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED;
        return DeviceLocationInfo(
            gpsEnabled = gpsEnabled,
            networkEnabled = networkEnabled,
            isFineLocationAccessGranted = isFineLocationAccessGranted,
            isCoarseLocationAccessGranted = isCoarseLocationAccessGranted
        )
    }


}

