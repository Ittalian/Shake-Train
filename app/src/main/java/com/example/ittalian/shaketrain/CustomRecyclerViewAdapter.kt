package com.example.ittalian.shaketrain

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults

class CustomRecyclerViewAdapter(realmResults: RealmResults<Course>) : RecyclerView.Adapter<ViewHolder>() {
    private var rResult: RealmResults<Course> = realmResults

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.one_result, parent, false)
        val viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun getItemCount() = rResult.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = rResult[position]
        holder.departStationText?.text = course?.departStaion.toString()
        holder.arriveStationText?.text = course?.arriveStation.toString()
        holder.indexNum?.text = (position + 1).toString()
        holder.itemView.setBackgroundColor(if (position % 2 == 0) Color.LTGRAY else Color.WHITE)
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, EditActivity::class.java)
            intent.putExtra("id", course?.id)
            it.context.startActivity(intent)
        }
    }
}