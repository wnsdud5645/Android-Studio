package com.hallym.weathercheck


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.hallym.weathercheck.databinding.LocationDialogBinding

class LocationActivity : AppCompatActivity() {

    lateinit var binding2 : LocationDialogBinding
    lateinit var sidoArr : MutableList<String>
    lateinit var sggArr : MutableList<String>
    lateinit var umdArr : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding2 = LocationDialogBinding.inflate(layoutInflater)
        setContentView(binding2.root)
        spinnerfuntionBinder()

        binding2.button2.setOnClickListener{
//          var intent : Intent = Intent()
//          intent.putExtra("name", "mike")
//          setResult(RESULT_OK, intent)
            setWeatherCondition()

            finish()

        }

    }

    fun spinnerfuntionBinder() {
        var temp = dc.sidoDB(db)!!
        temp.remove("")
        sidoArr = mutableListOf()
        sidoArr.add("시/도")
        sidoArr.addAll(temp)

        var adapterSido = ArrayAdapter(dc.getContext(), R.layout.custom_spinner_item,sidoArr)
        binding2.sidoSpinner.adapter = adapterSido

        sggArr = mutableListOf<String>()
        var adapterSgg = ArrayAdapter(dc.getContext(), R.layout.custom_spinner_item, sggArr)
        binding2.sggSpinner.adapter = adapterSgg

        umdArr = mutableListOf<String>()
        var adapterUmd = ArrayAdapter(dc.getContext(), R.layout.custom_spinner_item, umdArr)
        binding2.umdSpinner.adapter = adapterUmd

        binding2.sidoSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                if(position == 0)
                    return

                //Toast.makeText(this@MainActivity, sidoArr[position], Toast.LENGTH_SHORT).show()
                adapterSgg.clear()
                dc.userSido = sidoArr[position]
                temp = dc.sggDB(db, dc.userSido)!!
                temp.remove("")
                adapterSgg.add("시/군/구")
                adapterSgg.addAll(temp)
                Log.i("sidoSpinner","${dc.userSido}")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding2.sggSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0)
                    return

                //Toast.makeText(this@MainActivity, sggArr[position],Toast.LENGTH_SHORT).show()
                adapterUmd.clear()
                dc.userSgg = sggArr[position]
                temp = dc.umdDB(db, dc.userSido, dc.userSgg)!!
                temp.remove("")
                adapterUmd.add("읍/면/동")
                adapterUmd.addAll(temp)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding2.umdSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0)
                    return

                //Toast.makeText(this@MainActivity, umdArr[position], Toast.LENGTH_SHORT).show()
                dc.userUmd = umdArr[position]

                dc.setNWT(dc.totalDB(db,dc.userSido,dc.userSgg,dc.userUmd))

                dc.weatherAPIRun()
                dc.stationLocationAPIRun()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }

    fun setWeatherCondition(){

        binding.textView2.setText(dc.userSido + " " + dc.userSgg + " " + dc.userUmd ) //지역 설정
        binding.T1H.setText(dc.getWinfo().T1H + "°C") //기온
        binding.RN1.setText("강수량 "+dc.getWinfo().RN1 +" mm") //강수량
        binding.REH.setText("습도 "+dc.getWinfo().REH + "%")  //습도
        binding.UUU.setText("동서 " + dc.getWinfo().UUU + "m/s") //동서바람성분
        binding.VVV.setText("남북 " + dc.getWinfo().VVV + "m/s") //남북바람성분
        binding.VEC.setText("풍향 " + dc.getWinfo().VEC + "deg") //풍향
        binding.WSD.setText("풍속 " + dc.getWinfo().WSD + "m/s") //풍속
        binding.stationName.setText("대기오염 정보 제공 위치 : " + dc.getSlInfo().stationName) //스테이션
        binding.CO.setText("CO : " + dc.getAPInfo().CO) //이산화탄소

        var temp = ""
        if(dc.getAPInfo().PM10GRADE.toInt() == 1){
            temp = "좋음"
        }else if(dc.getAPInfo().PM10GRADE.toInt() == 2){
            temp = "보통"
        }else if(dc.getAPInfo().PM10GRADE.toInt() == 3){
            temp = "나쁨"
        }else{
            temp = "매우 나쁨"
        }

        binding.PM10.setText("미세먼지 : " + temp) //미세먼지

        if(dc.getAPInfo().PM25GRADE.toInt() == 1){
            temp = "좋음"
        }else if(dc.getAPInfo().PM25GRADE.toInt() == 2){
            temp = "보통"
        }else if(dc.getAPInfo().PM25GRADE.toInt() == 3){
            temp = "나쁨"
        }else{
            temp = "매우 나쁨"
        }
        binding.PM25.setText("초미세먼지 : " + temp) //초미세먼지

        //if(sky state == "?"){
        //set imageview => sky state
        //}

    }

}