package com.tristan.libtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StationEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
}
