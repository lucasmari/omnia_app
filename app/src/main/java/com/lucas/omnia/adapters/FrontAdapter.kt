package com.lucas.omnia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Front

class FrontAdapter(private val frontList: List<Front>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class FrontViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView: TextView = itemView.findViewById(R.id.front_tv_name_body)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FrontViewHolder(inflater.inflate(R.layout.item_front, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val (name) = frontList[position]
        val nameTextView = (viewHolder as FrontViewHolder).nameTextView
        nameTextView.text = name
    }

    override fun getItemCount(): Int {
        return frontList.size
    }
}