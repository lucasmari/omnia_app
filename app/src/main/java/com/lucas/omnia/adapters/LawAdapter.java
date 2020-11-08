package com.lucas.omnia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Entry;

import java.util.List;

public class LawAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Entry> lawList;

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView typeTextView;
        TextView dateTextView;
        TextView localityTextView;
        TextView authorityTextView;
        TextView titleTextView;
        TextView subjectTextView;
        TextView descriptionTextView;

        EntryViewHolder(View itemView) {
            super(itemView);

            typeTextView = itemView.findViewById(R.id.law_tv_type_body);
            dateTextView = itemView.findViewById(R.id.law_tv_date_body);
            localityTextView = itemView.findViewById(R.id.law_tv_locality_body);
            authorityTextView = itemView.findViewById(R.id.law_tv_authority_body);
            titleTextView = itemView.findViewById(R.id.law_tv_title_body);
            subjectTextView = itemView.findViewById(R.id.law_tv_subject_body);
            descriptionTextView = itemView.findViewById(R.id.law_tv_description_body);
        }
    }

    public LawAdapter(List<Entry> lawList) {
        this.lawList = lawList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new EntryViewHolder(inflater.inflate(R.layout.item_law, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Entry law = lawList.get(position);
        TextView typeTextView = ((EntryViewHolder) viewHolder).typeTextView;
        TextView dateTextView = ((EntryViewHolder) viewHolder).dateTextView;
        TextView localityTextView = ((EntryViewHolder) viewHolder).localityTextView;
        TextView authorityTextView = ((EntryViewHolder) viewHolder).authorityTextView;
        TextView titleTextView = ((EntryViewHolder) viewHolder).titleTextView;
        TextView subjectTextView = ((EntryViewHolder) viewHolder).subjectTextView;
        TextView descriptionTextView = ((EntryViewHolder) viewHolder).descriptionTextView;

        typeTextView.setText(law.type);
        dateTextView.setText(law.date);
        localityTextView.setText(law.locality);
        authorityTextView.setText(law.authority);
        titleTextView.setText(law.title);
        subjectTextView.setText(law.subject);
        descriptionTextView.setText(law.description);
    }

    @Override
    public int getItemCount() {
        return lawList.size();
    }
}