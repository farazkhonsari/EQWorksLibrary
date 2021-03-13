package com.eqworks.library.di

import com.eqworks.library.LocationLogger
import com.eqworks.library.data.DataSource
import com.eqworks.library.data.RemoteDataSourceFactory
import com.eqworks.library.data.Repository
import androidx.annotation.VisibleForTesting


object ServiceLocator {

    @Volatile
    internal var locationLogger: LocationLogger? = null
        @VisibleForTesting set

    internal fun provideLocationLogger(): LocationLogger {
        synchronized(this) {
            return locationLogger
                ?: createLocationLogger()
        }
    }

    private fun createRepository(): Repository {
        return Repository(createRemoteDataSource())
    }

    private fun createLocationLogger(): LocationLogger {
        return LocationLogger(createRepository())
    }

    private fun createRemoteDataSource(): DataSource {
        return RemoteDataSourceFactory.createRemoteDataSource()
    }


}