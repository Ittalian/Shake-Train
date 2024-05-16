package com.example.ittalian.shaketrain

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.view.ContentInfoCompat.Flags
import androidx.lifecycle.lifecycleScope
import io.realm.Realm
import io.realm.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class RecognizeShakeService : Service(), SensorEventListener {
    private val threshold: Float = 40f
    private var oldValue: Float = 0f
    private lateinit var realm: Realm

    override fun onCreate() {
        super.onCreate()

        realm = Realm.getDefaultInstance()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null)
            return
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            var shakeCount = 1
            val zDiff = Math.abs(event.values[0] - oldValue)
            if (zDiff > threshold) {
                Log.v("start", "検知！")
                val realmResults = realm.where(Course::class.java).findAll().sort("id", Sort.DESCENDING)
                if (realmResults.size >= shakeCount){
                    val mainUrl = "https://api.ekispert.jp/v1/json/search/course/light"
                    val apiKey = getString(R.string.api_key)
                    val departStation = realmResults[shakeCount - 1]?.departStaion
                    val arriveStation = realmResults[shakeCount - 1]?.arriveStation
                    val request = "$mainUrl?key=$apiKey&from=${departStation}&to=${arriveStation}"
                    courseTask(request)
                }
            }
            oldValue = event.values[2]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun courseTask(request: String) {
        GlobalScope.launch {
            val result = courseBackGroundTask(request)
            courseJsonTask(result)
        }
    }

    private suspend fun courseBackGroundTask(request: String) : String {
        val response = withContext(Dispatchers.IO) {
            var httpResult = ""

            try {
                val urlObj = URL(request)
                val br = BufferedReader(InputStreamReader(urlObj.openStream()))
                httpResult = br.readText()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return@withContext httpResult
        }

        return response
    }

    private fun courseJsonTask(result: String) {
        val jsonObj = JSONObject(result)
        val resourceUrl = jsonObj.getJSONObject("ResultSet").getString("ResourceURI")
        val uri = Uri.parse(resourceUrl)
        Log.v("uri", "$uri")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}