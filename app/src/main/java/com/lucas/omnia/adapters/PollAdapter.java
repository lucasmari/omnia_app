package com.lucas.omnia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Poll;

import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Poll> pollList;

    public static class PollViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView bodyTextView;
        TextView descriptionTextView;

        PollViewHolder(View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.poll_tv_date_body);
            bodyTextView = itemView.findViewById(R.id.poll_tv_body_label_body);
            descriptionTextView =
                    itemView.findViewById(R.id.poll_tv_description_abbreviation_body);
        }
    }

    public PollAdapter(List<Poll> pollList) {
        this.pollList = pollList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PollViewHolder(inflater.inflate(R.layout.item_poll, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Poll poll = pollList.get(position);
        TextView dateTextView = ((PollViewHolder) viewHolder).dateTextView;
        TextView bodyTextView = ((PollViewHolder) viewHolder).bodyTextView;
        TextView descriptionTextView = ((PollViewHolder) viewHolder).descriptionTextView;

        dateTextView.setText(poll.getDate());
        bodyTextView.setText(poll.getBody());
        descriptionTextView.setText(poll.getDescription());
    }

    @Override
    public int getItemCount() {
        return pollList.size();
    }
}