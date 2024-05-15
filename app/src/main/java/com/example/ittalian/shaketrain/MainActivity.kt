package com.example.ittalian.shaketrain

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ittalian.shaketrain.databinding.ActivityEditBinding
import com.example.ittalian.shaketrain.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private lateinit var realm: Realm
    private lateinit var adapter: CustomRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setSupportActionBar(_binding.toolbar)

        realm = Realm.getDefaultInstance()
        val mainUrl = "https://api.ekispert.jp/v1/json/search/course/light"
//        val departStation
//        val arriveStation
//        val request = "$mainUrl&key=$apiKey&${departStation.text}&${arriveStation.text}"

//        courseTask(request)

        _binding.toEditPage.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val realmResults = realm.where(Course::class.java).findAll().sort("id", Sort.DESCENDING)
        layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = layoutManager
        adapter = CustomRecyclerViewAdapter(realmResults)
        findViewById<RecyclerView>(R.id.recyclerView).adapter = this.adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun courseTask(mainUrl: String) {
        lifecycleScope.launch {
            val result = courseBackGroundTask(mainUrl)
            courseJsonTask(result)
        }
    }

    private suspend fun courseBackGroundTask(mainUrl: String) : String {
        val response = withContext(Dispatchers.IO) {
            var httpResult = ""

            try {
                val urlObj = URL(mainUrl)
                val br = BufferedReader(InputStreamReader(urlObj.openStream()))
                httpResult = br.readText()
            } catch (e:IOException) {
                e.printStackTrace()
            } catch (e:JSONException) {
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
        val intent = Intent(Intent.ACTION_VIEW,uri)

        startActivity(intent)
    }
}
