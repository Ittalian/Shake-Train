package com.example.ittalian.shaketrain

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ittalian.shaketrain.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.Sort

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
        val shakeIntent = Intent(this, RecognizeShakeService::class.java)
        startService(shakeIntent)
        _binding.serviceText.text = getString(R.string.starting_service_text)

        _binding.toEditPage.setOnClickListener {
            val editIntent = Intent(this, EditActivity::class.java)
            startActivity(editIntent)
        }

        _binding.finishBtn.setOnClickListener {
            val shakeIntent = Intent(this, RecognizeShakeService::class.java)
            stopService(shakeIntent)
            _binding.serviceText.text = getString(R.string.finish_service_text)
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
}