package com.hallym.weathercheck
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.hallym.weathercheck.databinding.ActivityMainBinding

lateinit var binding : ActivityMainBinding
lateinit var db : AppDatabase
lateinit var dc : DataCollection

class MainActivity : AppCompatActivity() {

    lateinit var sub : Thread_Sub

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(applicationContext)!!
        dc = DataCollection.getInstance(applicationContext,resources ,resources.assets, db, binding)
        sub = Thread_Sub(db, dc)
        sub.run()

//        val transform = Linkify.TransformFilter(){_, _ ->""}
//

        val URL1Str = "미세먼지 건강수칙"
        val URL2Str = "고농도 미세먼지 대처방법"
        val URL3Str = "미세먼지 많은 날! 실내청소법!"
        val URL4Str = "미세먼지 배출에 좋은 음식들"

//        Linkify.addLinks(binding.URL1, pattern1, "https://www.youtube.com/watch?v=52KvYxgmFbE", null, transform)
//        Linkify.addLinks(binding.URL2, pattern2, "https://www.youtube.com/watch?v=hCDmSBBDvvA", null, transform)
//        Linkify.addLinks(binding.URL3, pattern3, "https://www.youtube.com/watch?v=6YLv6NXMvT8", null, transform)
//        Linkify.addLinks(binding.URL4, pattern4, "https://www.youtube.com/watch?v=Fs299pSGT8k", null, transform)

        val intent = Intent(this, LocationActivity::class.java )

        binding.URL1.setText(Html.fromHtml(("<u>" + URL1Str + "</u>")))
        binding.URL2.setText(Html.fromHtml(("<u>" + URL2Str + "</u>")))
        binding.URL3.setText(Html.fromHtml(("<u>" + URL3Str + "</u>")))
        binding.URL4.setText(Html.fromHtml(("<u>" + URL4Str + "</u>")))

        binding.URL1.setOnClickListener{
            var intentUri = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=52KvYxgmFbE"))
            startActivity(intentUri)//        val pattern1 = Pattern.compile("미세먼지 건강수칙")
        }

        binding.URL2.setOnClickListener{
            var intentUri = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=hCDmSBBDvvA"))
            startActivity(intentUri)//        val pattern2 = Pattern.compile("고농도 미세먼지 대처방법")
        }

        binding.URL3.setOnClickListener{
            var intentUri = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=6YLv6NXMvT8"))
            startActivity(intentUri)//        val pattern3 = Pattern.compile("미세먼지 많은 날! 실내청소법!")
        }

        binding.URL4.setOnClickListener{
            var intentUri = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Fs299pSGT8k"))
            startActivity(intentUri)//        val pattern4 = Pattern.compile("미세먼지 배출에 좋은 음식들")
        }

        binding.imageButton.setOnClickListener {
            startActivity(intent)
            //stopLockTask()
        }
        binding.imageButton4.setOnClickListener {
            var intentUri = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cleanairnet.or.kr/main/main.html"))
            startActivity(intentUri)
        }

    }

}
