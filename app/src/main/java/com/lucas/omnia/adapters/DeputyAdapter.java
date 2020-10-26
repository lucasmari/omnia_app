package com.lucas.omnia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Deputy;

import java.util.List;

public class DeputyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Deputy> deputyList;

    public static class DeputyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView partyTextView;
        TextView stateTextView;
        TextView emailTextView;

        DeputyViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.deputy_tv_name_body);
            partyTextView = itemView.findViewById(R.id.deputy_tv_party_body);
            stateTextView =
                    itemView.findViewById(R.id.deputy_tv_state_body);
            emailTextView = itemView.findViewById(R.id.deputy_tv_email_body);
        }
    }

    public DeputyAdapter(List<Deputy> deputyList) {
        this.deputyList = deputyList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DeputyViewHolder(inflater.inflate(R.layout.item_deputy, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Deputy deputy = deputyList.get(position);
        TextView nameTextView = ((DeputyViewHolder) viewHolder).nameTextView;
        TextView partyTextView = ((DeputyViewHolder) viewHolder).partyTextView;
        TextView stateTextView = ((DeputyViewHolder) viewHolder).stateTextView;
        TextView emailTextView = ((DeputyViewHolder) viewHolder).emailTextView;

        nameTextView.setText(deputy.getName());
        partyTextView.setText(deputy.getParty());
        stateTextView.setText(deputy.getState());
        emailTextView.setText(deputy.getEmail());
    }

    @Override
    public int getItemCount() {
        return deputyList.size();
    }
}