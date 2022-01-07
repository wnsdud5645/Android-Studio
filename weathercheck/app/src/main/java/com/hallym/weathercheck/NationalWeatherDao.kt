package com.hallym.weathercheck

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NationalWeatherDao {
    @Query("SELECT * FROM app_database")
    fun getAll(): MutableList<NationalWeatherTable>

    @Query("SELECT DISTINCT sido FROM app_database")
    fun getSido(): MutableList<String>

    @Query("SELECT DISTINCT sgg FROM app_database WHERE sido = :selectedSido")
    fun getSgg(selectedSido: String): MutableList<String>

    @Query("SELECT umd FROM app_database WHERE sido = :selectedSido AND sgg = :selectedUmd")
    fun getUmd(selectedSido: String, selectedUmd: String): MutableList<String>

    @Query("SELECT * FROM app_database WHERE sido = :selectedSido AND sgg = :selectedUmd AND umd = :selectedSgg")
    fun getTotalInfo(selectedSido: String, selectedUmd: String, selectedSgg: String): NationalWeatherTable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(nationalWeatherTable: NationalWeatherTable)

    @Query("DELETE FROM app_database")
    fun deleteAll()
}