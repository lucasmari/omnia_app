package com.lucas.omnia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Block;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Block> blockList;

    public static class BlockViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;

        BlockViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.block_tv_name_body);
        }
    }

    public BlockAdapter(List<Block> blockList) {
        this.blockList = blockList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new BlockViewHolder(inflater.inflate(R.layout.item_block, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Block block = blockList.get(position);
        TextView nameTextView = ((BlockViewHolder) viewHolder).nameTextView;

        nameTextView.setText(block.getName());
    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }
}