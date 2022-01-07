package com.hallym.weathercheck

data class AirPolutionInformation(
    var CO : String, //일산화탄소
    var PM10 : String, //
    var PM25 : String,
    var COGRADE : String,
    var PM10GRADE : String,
    var PM25GRADE : String,
    val COFLAG : String,
    val PM10FLAG: String,
    val pm25FLAG: String
)

/*
<coValue>0.5</coValue>
<coGrade>1</coGrade>
<pm10Value>20</pm10Value>
<pm10Grade>1</pm10Grade>
<pm25Value>14</pm25Value>
<pm25Grade>2</pm25Grade>
 */