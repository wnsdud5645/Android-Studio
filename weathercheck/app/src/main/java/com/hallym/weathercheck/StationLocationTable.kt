package com.hallym.weathercheck

data class STATION(
    val response: RESPONSE2
)
data class RESPONSE2(
    val body: BODY2
)
data class BODY2(
    val totalCount: Int,
    val items : List<ITEMS2>
)
data class ITEMS2(
    val dmX: String,
    val dmY: String,
    val addr: String,
    val stationName: String,
)