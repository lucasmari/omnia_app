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
import com.lucas.omnia.models.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Event> eventList;

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView statusLabelTextView;
        TextView typeTextView;
        TextView descriptionTextView;
        TextView nameTextView;
        TextView buildingTextView;
        TextView roomTextView;
        TextView floorTextView;

        EventViewHolder(View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.event_tv_date_body);
            statusLabelTextView = itemView.findViewById(R.id.event_tv_status_body);
            typeTextView = itemView.findViewById(R.id.event_tv_type_body);
            descriptionTextView = itemView.findViewById(R.id.event_tv_description_body);
            nameTextView = itemView.findViewById(R.id.event_tv_name_body);
            buildingTextView = itemView.findViewById(R.id.event_tv_building_body);
            roomTextView = itemView.findViewById(R.id.event_tv_room_body);
            floorTextView = itemView.findViewById(R.id.event_tv_floor_body);
        }
    }

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new EventViewHolder(inflater.inflate(R.layout.item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Event event = eventList.get(position);
        TextView dateTextView = ((EventViewHolder) viewHolder).dateTextView;
        TextView statusLabelTextView = ((EventViewHolder) viewHolder).statusLabelTextView;
        TextView typeTextView = ((EventViewHolder) viewHolder).typeTextView;
        TextView descriptionTextView = ((EventViewHolder) viewHolder).descriptionTextView;
        TextView nameTextView = ((EventViewHolder) viewHolder).nameTextView;
        TextView buildingTextView = ((EventViewHolder) viewHolder).buildingTextView;
        TextView roomTextView = ((EventViewHolder) viewHolder).roomTextView;
        TextView floorTextView = ((EventViewHolder) viewHolder).floorTextView;

        dateTextView.setText(event.getDate());
        statusLabelTextView.setText(event.getStatus());
        typeTextView.setText(event.getType());
        descriptionTextView.setText(event.getDescription());
        nameTextView.setText(event.getName());
        buildingTextView.setText(event.getBuilding());
        roomTextView.setText(event.getRoom());
        floorTextView.setText(event.getFloor());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}