package com.hallym.weathercheck

data class WeatherInformation(
    var PTY : String,	//강수형태	코드값
    var REH : String,	//습도	%
    var RN1 : String,	//1시간 강수량	mm
    var T1H : String,	//기온	℃
    var UUU : String,	//동서바람성분	m/s
    var VEC : String,	//풍향	deg
    var VVV : String,	//남북바람성분	m/s
    var WSD : String	//풍속	m/s
)
