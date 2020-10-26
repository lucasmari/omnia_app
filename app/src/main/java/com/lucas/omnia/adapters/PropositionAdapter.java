package com.lucas.omnia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Proposition;

import java.util.List;

public class PropositionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Proposition> propositionList;

    public static class PropositionViewHolder extends RecyclerView.ViewHolder {
        TextView labelTextView;
        TextView yearTextView;
        TextView summaryTextView;

        PropositionViewHolder(View itemView) {
            super(itemView);

            labelTextView = itemView.findViewById(R.id.proposition_tv_label_body);
            yearTextView = itemView.findViewById(R.id.proposition_tv_year_label_body);
            summaryTextView =
                    itemView.findViewById(R.id.proposition_tv_summary_abbreviation_body);
        }
    }

    public PropositionAdapter(List<Proposition> propositionList) {
        this.propositionList = propositionList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PropositionViewHolder(inflater.inflate(R.layout.item_proposition, parent,
                false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Proposition proposition = propositionList.get(position);
        TextView labelTextView = ((PropositionViewHolder) viewHolder).labelTextView;
        TextView yearTextView = ((PropositionViewHolder) viewHolder).yearTextView;
        TextView summaryTextView = ((PropositionViewHolder) viewHolder).summaryTextView;

        labelTextView.setText(proposition.getLabel());
        yearTextView.setText(proposition.getYear());
        summaryTextView.setText(proposition.getSummary());
    }

    @Override
    public int getItemCount() {
        return propositionList.size();
    }
}