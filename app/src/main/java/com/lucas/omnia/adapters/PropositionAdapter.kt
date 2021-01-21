package com.lucas.omnia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Proposition

class PropositionAdapter(private val propositionList: List<Proposition>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class PropositionViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var labelTextView: TextView = itemView.findViewById(R.id.proposition_tv_label_body)
        var yearTextView: TextView = itemView.findViewById(R.id.proposition_tv_year_label_body)
        var summaryTextView: TextView = itemView.findViewById(R.id.proposition_tv_summary_abbreviation_body)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PropositionViewHolder(inflater.inflate(R.layout.item_proposition, parent,
                false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val (label, year, summary) = propositionList[position]
        val labelTextView = (viewHolder as PropositionViewHolder).labelTextView
        val yearTextView = viewHolder.yearTextView
        val summaryTextView = viewHolder.summaryTextView
        labelTextView.text = label
        yearTextView.text = year
        summaryTextView.text = summary
    }

    override fun getItemCount(): Int {
        return propositionList.size
    }
}