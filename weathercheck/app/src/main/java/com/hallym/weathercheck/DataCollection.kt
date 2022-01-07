package com.hallym.weathercheck

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import com.google.gson.GsonBuilder
import com.hallym.weathercheck.databinding.ActivityMainBinding
import com.hallym.weathercheck.databinding.LocationDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val gson = GsonBuilder().setLenient().create();
val retrofit_weather =  Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

object ApiObject_Weather {
    val retrofitService: WeatherInterface by lazy {
        retrofit_weather.create(WeatherInterface::class.java)
    }
}

val retrofit_Measuring_Station =  Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/")
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

object ApiObject_Measuring_Station {
    val retrofitService: StationLocationInterface by lazy {
        retrofit_Measuring_Station.create(StationLocationInterface::class.java)
    }
}

val retrofit_Air_pollution =  Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/")
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

object ApiObject_Air_pollution {
    val retrofitService: AirPolutionInterface by lazy {
        retrofit_Air_pollution.create(AirPolutionInterface::class.java)
    }
}

//싱글톤 클래스 : 유저 정보를 여기 저장
class DataCollection private constructor(){

    private var data_type = "JSON"
    private var num_of_rows = 0
    private var page_no = 0
    private var base_date = 0
    private var base_time = ""
    private var data_term = ""
    private var ver = ""

    private lateinit var nwt: NationalWeatherTable
    private lateinit var wInfo: WeatherInformation
    private lateinit var slInfo: StationLocationInformation
    private lateinit var apInfo: AirPolutionInformation

    var userSido = ""
    var userSgg = ""
    var userUmd = ""

    companion object{
        private var instance: DataCollection? = null
        private lateinit var context: Context
        private lateinit var resources: Resources
        private lateinit var assetManager: AssetManager
        private lateinit var db: AppDatabase
        private lateinit var binding: ActivityMainBinding

        // Context, resources.assets
        fun getInstance(_Context: Context, _Resources: Resources, _AssetManager: AssetManager, _db: AppDatabase, _binding: ActivityMainBinding): DataCollection
        {
            return instance ?: synchronized(this){
                instance ?: DataCollection().also{
                    context = _Context
                    resources = _Resources
                    assetManager = _AssetManager
                    db = AppDatabase.getInstance(_Context)!!
                    binding = _binding
                    instance = it
                }
            }
        }
    }

    fun getContext() : Context{
        return context
    }

    fun getResources() : Resources{
        return resources
    }

    fun getBinding() : ActivityMainBinding{
        return binding
    }

    fun getNWT() : NationalWeatherTable{
        return nwt
    }

    fun getWinfo() : WeatherInformation{
        return wInfo
    }

    fun getSlInfo() : StationLocationInformation{
        return slInfo
    }

    fun getAPInfo() : AirPolutionInformation{
        return apInfo
    }

    fun setNWT(_nwt: NationalWeatherTable) {
        nwt = _nwt
    }


    fun weatherAPIRun(){
        num_of_rows = 8
        page_no = 1
        var today= refreshDate().split("\t")
        base_date = today[0].toInt()
        base_time = today[1]
        val call = ApiObject_Weather.retrofitService.GetWeather(data_type, num_of_rows, page_no, base_date, base_time, nwt.nx.toString(), nwt.ny.toString())
        call.enqueue(object : retrofit2.Callback<WEATHER>{
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful){
                    //Log.d("api", response.toString())
                    //Log.d("api", response.body().toString())
                    //Log.d("api", response.body()!!.response.body.items.item.toString())
                    //Log.d("api", response.body()!!.response.body.items.item[0].category)
                    wInfo = WeatherInformation("","","","","","","","")
                    var tempArr = response.body()!!.response.body.items.item
                    var i = 0;
                    while(i < tempArr.size)
                    {
                        when(tempArr[i].category)
                        {
                            "PTY" -> wInfo.PTY = tempArr[i].obsrValue//강수형태	코드값
                            "REH" -> wInfo.REH = tempArr[i].obsrValue//습도	%
                            "RN1" -> wInfo.RN1 = tempArr[i].obsrValue//1시간 강수량	mm
                            "T1H" -> wInfo.T1H = tempArr[i].obsrValue//기온	℃
                            "UUU" -> wInfo.UUU = tempArr[i].obsrValue//동서바람성분	m/s
                            "VEC" -> wInfo.VEC = tempArr[i].obsrValue//풍향	deg
                            "VVV" -> wInfo.VVV = tempArr[i].obsrValue//남북바람성분	m/s
                            "WSD" -> wInfo.WSD = tempArr[i].obsrValue//풍속	m/s
                        }
                        Log.i("NWT result : " , "${tempArr[i].category} = ${tempArr[i].obsrValue}")
                        i++
                    }
                }
            }

            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                t.message?.let { Log.d("api fail:", it) }
            }
        })
    }



    fun stationLocationAPIRun(){
        num_of_rows = 200
        page_no = 1
        var stationSido : String = ""
        if(nwt.sido.slice(0..1) == "서울"){
            stationSido = "서울"
        }else if(nwt.sido.slice(0..2) == "전라남"){
            stationSido = "전남"
        }else if(nwt.sido.slice(0..2) == "전라북"){
            stationSido = "전북"
        }else if(nwt.sido.slice(0..2)=="충청남"){
            stationSido = "충남"
        }else if(nwt.sido.slice(0..2)=="충청북"){
            stationSido = "충북"
        }else if(nwt.sido.slice(0..2)=="경상남"){
            stationSido = "경남"
        }else if(nwt.sido.slice(0..2)=="경상북"){
            stationSido = "경북"
        }else{
            stationSido = nwt.sido.slice(0..2)
        }
        val call = ApiObject_Measuring_Station.retrofitService.GetStationLocation(data_type,num_of_rows,page_no,stationSido)
        call.enqueue(object : retrofit2.Callback<STATION>{
            override fun onResponse(call: Call<STATION>, response: Response<STATION>) {
                if (response.isSuccessful){

                    // 예외처리 서울특별시, 전라남도, 전라북도, 충청남도, 충청북도, 경상남도, 경상북도 => 서울특 => 서울, 전라남도 => 전남
                    Log.d("api", response.toString())
                    Log.d("api", response.body().toString())
                    Log.d("api", response.body()!!.response.body.items.toString())
                    Log.d("api",response.body()!!.response.body.items[0].stationName)
                    Log.d("api",response.body()!!.response.body.items[0].addr.slice(0..2))
                    slInfo = StationLocationInformation("","","","")

                    var temp_arr = response.body()!!.response.body.items
                    var i = 0
                    var temp: Array<String> = arrayOf("","","","")
                    var temp_dist= Double.MAX_VALUE
                    var Loc1 = nwt.longitude+nwt.latitude

                    while(i < temp_arr.size)
                    {
                        var Loc2 = temp_arr[i].dmX.toDouble()+ temp_arr[i].dmY.toDouble()
                        if(Loc2 > Loc1)
                            if(Loc2-Loc1 < temp_dist)
                            {
                                temp_dist = Loc2-Loc1
                                temp = arrayOf(temp_arr[i].dmX,temp_arr[i].dmY,temp_arr[i].addr,temp_arr[i].stationName)
                            }
                            else if(Loc1 > Loc2) {
                                if (Loc1 - Loc2 < temp_dist) {
                                    temp_dist = Loc1 - Loc2
                                    temp = arrayOf(temp_arr[i].dmX,temp_arr[i].dmY,temp_arr[i].addr,temp_arr[i].stationName)
                                }
                            }
                            else if(Loc1 == Loc2){
                                temp = arrayOf(temp_arr[i].dmX,temp_arr[i].dmY,temp_arr[i].addr,temp_arr[i].stationName)
                            }
                        i++
                    }

                    slInfo.dmX = temp[0]
                    slInfo.dmY = temp[1]
                    slInfo.addr = temp[2]
                    slInfo.stationName = temp[3]
                    Log.i("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "${slInfo.stationName}")
                    airPolutionAPIRun()
                }
            }
            override fun onFailure(call: Call<STATION>, t: Throwable) {
                t.message?.let { Log.d("api fail:", it) }
            }
        })
    }

    fun airPolutionAPIRun(){
        num_of_rows = 1
        page_no = 1
        data_term = "DAILY"
        ver = "1.0"
        val call = ApiObject_Air_pollution.retrofitService.GetAirPolution(data_type, num_of_rows, page_no, slInfo.stationName,data_term, ver)
        call.enqueue(object : retrofit2.Callback<AIRPOLUTION>{
            override fun onResponse(call: Call<AIRPOLUTION>, response: Response<AIRPOLUTION>) {
                if (response.isSuccessful){
                    Log.d("api", response.toString())
                    Log.d("api", response.body().toString())
                    apInfo = AirPolutionInformation("","","","","","","","","")
                    var temp_arr = response.body()!!.response.body.items
                    apInfo.CO = temp_arr[0].coValue//coValue
                    apInfo.PM10 = temp_arr[0].pm10Value//pm10Value
                    apInfo.PM25 = temp_arr[0].pm25Value//pm25Value
                    if(temp_arr[0].coFlag == null)
                        apInfo.COGRADE = temp_arr[0].coGrade//coGrade
                    if(temp_arr[0].pm10Flag == null)
                        apInfo.PM10GRADE = temp_arr[0].pm10Grade//pm10Grade
                    if(temp_arr[0].pm25Flag == null)
                        apInfo.PM25GRADE = temp_arr[0].pm25Grade//pm25Grade
                }
            }

            override fun onFailure(call: Call<AIRPOLUTION>, t: Throwable) {
                t.message?.let { Log.d("api fail:", it) }
            }
        })
    }


    private fun refreshDate():String{
        var result : String
        val current = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        var date = current.format(formatter)
        var time: String
        formatter = DateTimeFormatter.ofPattern("mm")
        var minute = current.format(formatter).toInt()
        formatter = DateTimeFormatter.ofPattern("HH00")
        time = current.format(formatter)
        if(minute <= 30) {
            if(time == "0000")
                time = "2300"
            else {
                if(time.toInt() - 100 < 1000)
                    time = "0" + (time.toInt() - 100).toString()
                else
                    time = (time.toInt() - 100).toString()
            }
        }

        Log.i("DATECHEECK", "$date\t$time")
        result = "$date\t$time"
        return result
    }

    fun initDB(){
        val inputstream: InputStream = assetManager.open("NationalWeatherDB.txt")
        inputstream.bufferedReader().readLines().forEach {
            var token = it.split("\t")
            var input = NationalWeatherTable(
                token[0].toLong(),
                token[1],
                token[2],
                token[3],
                token[4].toInt(),
                token[5].toInt(),
                token[12].toDouble(),
                token[13].toDouble())

            //CoroutineScope(Dispatchers.IO).launch {
            runBlocking(Dispatchers.IO) {
                db.nationalWeatherInterface().insert(input)
            }
        }
        //CoroutineScope(Dispatchers.Default).launch {
        runBlocking(Dispatchers.Default) {
            var output = db.nationalWeatherInterface().getAll()
            Log.i("!!!DATABASEINITALIZE!!!", "$output")
        }
    }

    fun sidoDB(db: AppDatabase) : MutableList<String>?{
        var result: MutableList<String>? = null
        //시도
        runBlocking(Dispatchers.Default) {
            var output = db.nationalWeatherInterface().getSido()
            output.forEach {
                Log.i("SidoTest", it)
            }
            result = output
        }
        return result
    }

    fun sggDB(db: AppDatabase, sido: String) : MutableList<String>{
        var result: MutableList<String>
        //시군구, selected 에 읍면동에서 선택한거 넣기
        runBlocking(Dispatchers.Default) {
            var output = db.nationalWeatherInterface().getSgg(sido)
            output.forEach {
                Log.i("SggTest", it)
            }
            result = output
        }
        return result
    }

    fun umdDB(db: AppDatabase ,sido: String ,sgg: String) : MutableList<String>{
        var result: MutableList<String>
        //읍면동, selected 에 시도에서 선택한거 넣기
        runBlocking(Dispatchers.Default) {
            var output = db.nationalWeatherInterface().getUmd(sido,sgg)
            output.forEach {
                Log.i("UmdTest", it)
            }
            result = output
        }
        return result
    }

    fun totalDB(db: AppDatabase, sido: String, sgg: String, umd: String) : NationalWeatherTable{
        var result: NationalWeatherTable
        runBlocking(Dispatchers.Default) {
            var output = db.nationalWeatherInterface().getTotalInfo(sido, sgg, umd)
            Log.i("TotalTest", "$output")
            result = output
            nwt = result
        }
        return result
    }
}