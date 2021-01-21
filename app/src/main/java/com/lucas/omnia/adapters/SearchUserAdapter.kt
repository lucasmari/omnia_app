package com.lucas.omnia.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.activities.UserPageActivity
import com.lucas.omnia.models.User

class SearchUserAdapter(private val context: Context, private val userList: List<User>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class UserViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var userTextView: TextView = itemView.findViewById(R.id.user_tv_name)
        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val (uid) = userList[position]
                val intent = Intent(context, UserPageActivity::class.java)
                intent.putExtra(UserPageActivity.EXTRA_USER_KEY, uid)
                context.startActivity(intent)
            }
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UserViewHolder(inflater.inflate(R.layout.item_user, parent,
                false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val userTextView = (viewHolder as UserViewHolder).userTextView
        val (_, username) = userList[position]
        userTextView.text = username
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}