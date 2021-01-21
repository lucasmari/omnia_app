package com.lucas.omnia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Party

class PartyAdapter(private val partyList: List<Party>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class PartyViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var labelTextView: TextView = itemView.findViewById(R.id.party_tv_label_body)
        var nameTextView: TextView = itemView.findViewById(R.id.party_tv_name_body)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PartyViewHolder(inflater.inflate(R.layout.item_party, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val (label, name) = partyList[position]
        val labelTextView = (viewHolder as PartyViewHolder).labelTextView
        val nameTextView = viewHolder.nameTextView
        labelTextView.text = label
        nameTextView.text = name
    }

    override fun getItemCount(): Int {
        return partyList.size
    }
}