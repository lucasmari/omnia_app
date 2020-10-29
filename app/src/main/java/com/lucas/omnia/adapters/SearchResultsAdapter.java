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
import com.lucas.omnia.activities.UserPageActivity;
import com.lucas.omnia.models.User;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<User> userList;
    private final Context context;

    public SearchResultsAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView userTextView;

        UserViewHolder(View itemView) {
            super(itemView);

            userTextView = itemView.findViewById(R.id.user_tv_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                User user = userList.get(position);

                Intent intent = new Intent(context, UserPageActivity.class);
                intent.putExtra(UserPageActivity.EXTRA_USER_KEY, user.uid);
                context.startActivity(intent);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new UserViewHolder(inflater.inflate(R.layout.item_user, parent,
                false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TextView userTextView =
                ((UserViewHolder) viewHolder).userTextView;
        User user = userList.get(position);
        userTextView.setText(user.username);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}