package com.lucas.omnia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Block

class BlockAdapter(private val blockList: List<Block>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class BlockViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView: TextView = itemView.findViewById(R.id.block_tv_name_body)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BlockViewHolder(inflater.inflate(R.layout.item_block, parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val (name) = blockList[position]
        val nameTextView = (viewHolder as BlockViewHolder).nameTextView
        nameTextView.text = name
    }

    override fun getItemCount(): Int {
        return blockList.size
    }
}