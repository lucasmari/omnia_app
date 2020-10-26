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
import com.lucas.omnia.models.Front;

import java.util.List;

public class FrontAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Front> frontList;

    public static class FrontViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        FrontViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.front_tv_name_body);
        }
    }

    public FrontAdapter(List<Front> frontList) {
        this.frontList = frontList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FrontViewHolder(inflater.inflate(R.layout.item_front, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Front front = frontList.get(position);
        TextView nameTextView = ((FrontViewHolder) viewHolder).nameTextView;

        nameTextView.setText(front.getName());
    }

    @Override
    public int getItemCount() {
        return frontList.size();
    }
}