package com.example.ittalian.shaketrain

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.coroutineContext

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var departStationText: TextView? = null
    var arriveStationText: TextView? = null
    var indexNum: TextView? = null

    init {
        departStationText = itemView.findViewById(R.id.departStationText)
        arriveStationText = itemView.findViewById(R.id.arriveStationText)
        indexNum = itemView.findViewById(R.id.indexNum)
    }
}