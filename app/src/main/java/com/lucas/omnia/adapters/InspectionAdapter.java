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
import com.lucas.omnia.models.Inspection;

import java.util.List;

public class InspectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Inspection> inspectionList;
    private final Context context;

    public class InspectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView inspectionTextView;

        InspectionViewHolder(View itemView) {
            super(itemView);

            inspectionTextView = itemView.findViewById(R.id.inspection_tv_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Inspection inspection = inspectionList.get(position);

                Intent intent = new Intent(context, inspection.getActivityClass());
                context.startActivity(intent);
            }
        }
    }

    public InspectionAdapter(Context context, List<Inspection> inspectionList) {
        this.context = context;
        this.inspectionList = inspectionList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new InspectionViewHolder(inflater.inflate(R.layout.item_inspection, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TextView inspectionTextView = ((InspectionViewHolder) viewHolder).inspectionTextView;
        Inspection inspection = inspectionList.get(position);
        inspectionTextView.setText(inspection.getName());
    }

    @Override
    public int getItemCount() {
        return inspectionList.size();
    }
}