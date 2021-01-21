package com.lucas.omnia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Body

class BodyAdapter(private val bodyList: List<Body>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class BodyViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var labelTextView: TextView = itemView.findViewById(R.id.body_tv_label_body)
        var nameTextView: TextView = itemView.findViewById(R.id.body_tv_name_label_body)
        var aliasTextView: TextView = itemView.findViewById(R.id.body_tv_alias_body)
        var typeTextView: TextView = itemView.findViewById(R.id.body_tv_type_body)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BodyViewHolder(inflater.inflate(R.layout.item_body, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val (label, name, alias, type) = bodyList[position]
        val labelTextView = (viewHolder as BodyViewHolder).labelTextView
        val nameTextView = viewHolder.nameTextView
        val aliasTextView = viewHolder.aliasTextView
        val typeTextView = viewHolder.typeTextView
        labelTextView.text = label
        nameTextView.text = name
        aliasTextView.text = alias
        typeTextView.text = type
    }

    override fun getItemCount(): Int {
        return bodyList.size
    }
}