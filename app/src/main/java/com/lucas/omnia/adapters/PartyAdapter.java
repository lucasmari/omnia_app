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
import com.lucas.omnia.models.Party;

import java.util.List;

public class PartyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Party> partyList;

    public static class PartyViewHolder extends RecyclerView.ViewHolder {
        public TextView labelTextView;
        public TextView nameTextView;

        PartyViewHolder(View itemView) {
            super(itemView);

            labelTextView = itemView.findViewById(R.id.party_tv_label_body);
            nameTextView = itemView.findViewById(R.id.party_tv_name_body);
        }
    }

    public PartyAdapter(List<Party> partyList) {
        this.partyList = partyList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PartyViewHolder(inflater.inflate(R.layout.item_party, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Party party = partyList.get(position);
        TextView labelTextView = ((PartyViewHolder) viewHolder).labelTextView;
        TextView nameTextView = ((PartyViewHolder) viewHolder).nameTextView;


        labelTextView.setText(party.getLabel());
        nameTextView.setText(party.getName());
    }

    @Override
    public int getItemCount() {
        return partyList.size();
    }
}