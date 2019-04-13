package com.example.simpleweather

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import kotlinx.android.synthetic.main.activity_main.*


@SuppressLint("Registered")
class CuacaHariIni : JobService() {
    override fun onStartJob(job: JobParameters): Boolean {
        getCuacaHariIni(job)
        return true
    }

    val TAG = CuacaHariIni::class.java.simpleName
    val AppID = "a0e0b98b89856ef24a67bc869f6ccc2f"
    val Kota = MainActivity().spinnerKota

    override fun onStopJob(job: com.firebase.jobdispatcher.JobParameters): Boolean {
        Log.d(TAG,"Stop Job")
        return true;
    }
    fun getCuacaHariIni(p0: JobParameters?) {
        if(p0!=null) {
            var client = AsyncHttpClient()
            var url = "https://api.openweathermap.org/data/2.5/weather?" + "q=$Kota&appid=$AppID"
            val charset = Charsets.UTF_8
            var handler = object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                    var result: String = ""
                    if (responseBody != null) {
                        result = responseBody.toString(charset)
                    }

                    // Push Notification
                    val obj = JSONObject(result)
                    val weather = obj.getJSONArray("weather").getJSONObject(0)
                    var channel_id = weather.getString("id")
                    var subTitle = weather.getString("main")
                    var description = weather.getString("description")
                    var suhu = obj.getJSONObject("main")
                    var suhuMin = suhu.getString("temp_min")
                    var suhuMax = suhu.getString("temp_max")


                    var mBuilder = NotificationCompat.Builder(this@CuacaHariIni, channel_id)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Cuaca Hari Ini di " + Kota)
                        .setContentText(subTitle)
                        .setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText(subTitle + "\nDeskripsi : " + description + "\nSuhu : " + suhuMin + " - " + suhuMax)
                        )
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val id = 30103
                    mNotificationManager.notify(id, mBuilder.build())
                    jobFinished(p0, false)
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray?,
                    error: Throwable?) {
                    jobFinished(p0, true)
                    Log.d(TAG, "Failed")
                }
            }
            client.get(url, handler)
        }
    }

}