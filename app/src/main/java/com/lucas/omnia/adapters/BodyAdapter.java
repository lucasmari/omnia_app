package com.lucas.omnia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Body;

import java.util.List;

public class BodyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Body> bodyList;

    public static class BodyViewHolder extends RecyclerView.ViewHolder {
        TextView labelTextView;
        TextView nameTextView;
        TextView aliasTextView;
        TextView typeTextView;

        BodyViewHolder(View itemView) {
            super(itemView);

            labelTextView = itemView.findViewById(R.id.body_tv_label_body);
            nameTextView = itemView.findViewById(R.id.body_tv_name_label_body);
            aliasTextView =
                    itemView.findViewById(R.id.body_tv_alias_body);
            typeTextView = itemView.findViewById(R.id.body_tv_type_body);
        }
    }

    public BodyAdapter(List<Body> bodyList) {
        this.bodyList = bodyList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new BodyViewHolder(inflater.inflate(R.layout.item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Body body = bodyList.get(position);
        TextView labelTextView = ((BodyViewHolder) viewHolder).labelTextView;
        TextView nameTextView = ((BodyViewHolder) viewHolder).nameTextView;
        TextView aliasTextView = ((BodyViewHolder) viewHolder).aliasTextView;
        TextView typeTextView = ((BodyViewHolder) viewHolder).typeTextView;

        labelTextView.setText(body.getLabel());
        nameTextView.setText(body.getName());
        aliasTextView.setText(body.getAlias());
        typeTextView.setText(body.getType());
    }

    @Override
    public int getItemCount() {
        return bodyList.size();
    }
}