package com.lucas.omnia.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.activities.LawPageActivity
import com.lucas.omnia.models.Law

class SearchLawAdapter(private val context: Context, private val lawList: List<Law>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class LawViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var localeTextView: TextView = itemView.findViewById(R.id.law_tv_locale_body)
        var authorityTextView: TextView = itemView.findViewById(R.id.law_tv_authority_body)
        var titleTextView: TextView = itemView.findViewById(R.id.law_tv_title_body)
        var descriptionTextView: TextView = itemView.findViewById(R.id.law_tv_description_body)
        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val (urn) = lawList[position]
                val intent = Intent(context, LawPageActivity::class.java)
                intent.putExtra(LawPageActivity.EXTRA_LAW_URN, urn)
                context.startActivity(intent)
            }
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LawViewHolder(inflater.inflate(R.layout.item_law, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val (_, locale, authority, title, description) = lawList[position]
        val localeTextView = (viewHolder as LawViewHolder).localeTextView
        val authorityTextView = viewHolder.authorityTextView
        val titleTextView = viewHolder.titleTextView
        val descriptionTextView = viewHolder.descriptionTextView
        localeTextView.text = locale
        authorityTextView.text = authority
        titleTextView.text = title
        descriptionTextView.text = description
    }

    override fun getItemCount(): Int {
        return lawList.size
    }
}