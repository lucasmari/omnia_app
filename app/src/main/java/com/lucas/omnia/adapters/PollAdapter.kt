package com.lucas.omnia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Poll

class PollAdapter(private val pollList: List<Poll>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class PollViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateTextView: TextView = itemView.findViewById(R.id.poll_tv_date_body)
        var bodyTextView: TextView = itemView.findViewById(R.id.poll_tv_body_label_body)
        var descriptionTextView: TextView = itemView.findViewById(R.id.poll_tv_description_abbreviation_body)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PollViewHolder(inflater.inflate(R.layout.item_poll, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val (date, body, description) = pollList[position]
        val dateTextView = (viewHolder as PollViewHolder).dateTextView
        val bodyTextView = viewHolder.bodyTextView
        val descriptionTextView = viewHolder.descriptionTextView
        dateTextView.text = date
        bodyTextView.text = body
        descriptionTextView.text = description
    }

    override fun getItemCount(): Int {
        return pollList.size
    }
}