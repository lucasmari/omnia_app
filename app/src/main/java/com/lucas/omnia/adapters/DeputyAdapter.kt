package com.lucas.omnia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Deputy

class DeputyAdapter(private val deputyList: List<Deputy>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class DeputyViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView: TextView = itemView.findViewById(R.id.deputy_tv_name_body)
        var partyTextView: TextView = itemView.findViewById(R.id.deputy_tv_party_body)
        var stateTextView: TextView = itemView.findViewById(R.id.deputy_tv_state_body)
        var emailTextView: TextView = itemView.findViewById(R.id.deputy_tv_email_body)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DeputyViewHolder(inflater.inflate(R.layout.item_deputy, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val (name, party, state, email) = deputyList[position]
        val nameTextView = (viewHolder as DeputyViewHolder).nameTextView
        val partyTextView = viewHolder.partyTextView
        val stateTextView = viewHolder.stateTextView
        val emailTextView = viewHolder.emailTextView
        nameTextView.text = name
        partyTextView.text = party
        stateTextView.text = state
        emailTextView.text = email
    }

    override fun getItemCount(): Int {
        return deputyList.size
    }
}