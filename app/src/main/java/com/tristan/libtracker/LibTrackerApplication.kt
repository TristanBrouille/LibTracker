package com.tristan.libtracker

import android.app.Application
import androidx.room.Room
import com.tristan.libtracker.data.api.VelibService
import com.tristan.libtracker.data.local.AppDatabase
import com.tristan.libtracker.data.location.LocationClient
import com.tristan.libtracker.data.repository.StationRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LibTrackerApplication : Application() {
    lateinit var stationRepository: StationRepository
    lateinit var locationClient: LocationClient

    override fun onCreate() {
        super.onCreate()
        
        locationClient = LocationClient(this)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "libtracker-db"
        ).fallbackToDestructiveMigration().build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://velib-metropole-opendata.smovengo.cloud/opendata/Velib_Metropole/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val velibService = retrofit.create(VelibService::class.java)

        stationRepository = StationRepository(velibService, db.stationDao())
    }
}
