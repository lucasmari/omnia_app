package com.lucas.omnia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Event

class EventAdapter(private val eventList: List<Event>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class EventViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateTextView: TextView = itemView.findViewById(R.id.event_tv_date_body)
        var statusLabelTextView: TextView = itemView.findViewById(R.id.event_tv_status_body)
        var typeTextView: TextView = itemView.findViewById(R.id.event_tv_type_body)
        var descriptionTextView: TextView = itemView.findViewById(R.id.event_tv_description_body)
        var nameTextView: TextView = itemView.findViewById(R.id.event_tv_name_body)
        var buildingTextView: TextView = itemView.findViewById(R.id.event_tv_building_body)
        var roomTextView: TextView = itemView.findViewById(R.id.event_tv_room_body)
        var floorTextView: TextView = itemView.findViewById(R.id.event_tv_floor_body)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EventViewHolder(inflater.inflate(R.layout.item_event, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val (date, status, type, description, name, building, room, floor) = eventList[position]
        val dateTextView = (viewHolder as EventViewHolder).dateTextView
        val statusLabelTextView = viewHolder.statusLabelTextView
        val typeTextView = viewHolder.typeTextView
        val descriptionTextView = viewHolder.descriptionTextView
        val nameTextView = viewHolder.nameTextView
        val buildingTextView = viewHolder.buildingTextView
        val roomTextView = viewHolder.roomTextView
        val floorTextView = viewHolder.floorTextView
        dateTextView.text = date
        statusLabelTextView.text = status
        typeTextView.text = type
        descriptionTextView.text = description
        nameTextView.text = name
        buildingTextView.text = building
        roomTextView.text = room
        floorTextView.text = floor
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}