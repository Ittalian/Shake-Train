package com.example.ittalian.shaketrain

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ittalian.shaketrain.databinding.ActivityEditBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import org.json.JSONObject

class EditActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    private lateinit var _binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        realm = Realm.getDefaultInstance()

        val courseId = intent.getLongExtra("id", 0L)
        if (courseId > 0) {
            val course = realm.where<Course>().equalTo("id", courseId).findFirst()
            _binding.departStation.setText(course?.departStaion.toString())
            _binding.arriveStation.setText(course?.arriveStation.toString())
            _binding.deletebtn.visibility = View.VISIBLE
        } else {
            _binding.deletebtn.visibility = View.INVISIBLE
        }

        _binding.savebtn.setOnClickListener {
            var departStation: String = ""
            var arriveStation: String = ""

            if (!_binding.departStation.text.isNullOrEmpty())
                departStation = _binding.departStation.text.toString()
            if (!_binding.arriveStation.text.isNullOrEmpty())
                arriveStation = _binding.arriveStation.text.toString()

            when (courseId) {
                0L -> {
                    realm.executeTransaction {
                        val maxId = realm.where<Course>().max("id")
                        val nextId = (maxId?.toLong() ?: 0L) + 1L
                        val course = realm.createObject<Course>(nextId)
                        course?.departStaion = departStation
                        course?.arriveStation = arriveStation
                    }
                }

                else -> {
                    realm.executeTransaction {
                        val course = realm.where<Course>().equalTo("id", courseId).findFirst()
                        course?.departStaion = departStation
                        course?.arriveStation = arriveStation
                    }
                }
            }
            Toast.makeText(applicationContext, "保存しました", Toast.LENGTH_SHORT).show()
            finish()
        }

        _binding.deletebtn.setOnClickListener {
            realm.executeTransaction {
                val course = realm.where<Course>().equalTo("id", courseId)?.findFirst()?.deleteFromRealm()
            }
            Toast.makeText(applicationContext, "削除しました", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}