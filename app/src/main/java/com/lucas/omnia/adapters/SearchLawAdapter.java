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
import com.lucas.omnia.activities.LawPageActivity;
import com.lucas.omnia.models.Law;

import java.util.List;

public class SearchLawAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Law> lawList;
    private final Context context;

    public SearchLawAdapter(Context context, List<Law> lawList) {
        this.context = context;
        this.lawList = lawList;
    }

    public class LawViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView localeTextView;
        TextView authorityTextView;
        TextView titleTextView;
        TextView descriptionTextView;

        LawViewHolder(View itemView) {
            super(itemView);

            localeTextView = itemView.findViewById(R.id.law_tv_locale_body);
            authorityTextView = itemView.findViewById(R.id.law_tv_authority_body);
            titleTextView = itemView.findViewById(R.id.law_tv_title_body);
            descriptionTextView = itemView.findViewById(R.id.law_tv_description_body);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Law law = lawList.get(position);

                Intent intent = new Intent(context, LawPageActivity.class);
                intent.putExtra(LawPageActivity.EXTRA_LAW_URN, law.getUrn());
                context.startActivity(intent);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new LawViewHolder(inflater.inflate(R.layout.item_law, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Law law = lawList.get(position);
        TextView localeTextView = ((LawViewHolder) viewHolder).localeTextView;
        TextView authorityTextView = ((LawViewHolder) viewHolder).authorityTextView;
        TextView titleTextView = ((LawViewHolder) viewHolder).titleTextView;
        TextView descriptionTextView = ((LawViewHolder) viewHolder).descriptionTextView;

        localeTextView.setText(law.getLocale());
        authorityTextView.setText(law.getAuthority());
        titleTextView.setText(law.getTitle());
        descriptionTextView.setText(law.getDescription());
    }

    @Override
    public int getItemCount() {
        return lawList.size();
    }
}