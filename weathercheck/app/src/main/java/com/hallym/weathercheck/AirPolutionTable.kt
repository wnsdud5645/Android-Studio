package com.hallym.weathercheck

data class AIRPOLUTION(
    val response: RESPONSE3
)
data class RESPONSE3(
    val body: BODY3
)
data class BODY3(
    val totalCount: Int,
    val items : List<ITEM3>
)
data class ITEM3(
    val coValue : String,
    val pm10Value : String,
    val pm25Value : String,
    val coGrade : String,
    val pm10Grade : String,
    val pm25Grade : String,
    val coFlag : String,
    val pm10Flag: String,
    val pm25Flag: String
)