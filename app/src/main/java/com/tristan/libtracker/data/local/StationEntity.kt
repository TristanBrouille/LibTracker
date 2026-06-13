package com.tristan.libtracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val bikesAvailable: Int,
    val mechanicalBikes: Int,
    val electricBikes: Int,
    val docksAvailable: Int,
    val isFavorite: Boolean,
    val lastUpdated: Long
)
