package com.hallym.weathercheck

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_database")
data class NationalWeatherTable(
    @PrimaryKey val code: Long,
    @ColumnInfo(name = "sido")val sido: String,
    @ColumnInfo(name = "sgg")val sgg: String,
    @ColumnInfo(name = "umd")val umd: String,
    @ColumnInfo(name = "nx")val nx: Int,
    @ColumnInfo(name = "ny")val ny: Int,
    @ColumnInfo(name = "longitude")val longitude: Double,
    @ColumnInfo(name = "latitude")val latitude: Double
)
