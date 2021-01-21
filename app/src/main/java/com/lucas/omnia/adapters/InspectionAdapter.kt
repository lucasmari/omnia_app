package com.lucas.omnia.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Inspection

class InspectionAdapter(private val context: Context, private val inspectionList: List<Inspection>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class InspectionViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var inspectionTextView: TextView = itemView.findViewById(R.id.inspection_tv_title)
        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val (_, activityClass) = inspectionList[position]
                val intent = Intent(context, activityClass)
                context.startActivity(intent)
            }
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return InspectionViewHolder(inflater.inflate(R.layout.item_inspection, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val inspectionTextView = (viewHolder as InspectionViewHolder).inspectionTextView
        val (name) = inspectionList[position]
        inspectionTextView.text = name
    }

    override fun getItemCount(): Int {
        return inspectionList.size
    }
}