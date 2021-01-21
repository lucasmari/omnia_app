package com.lucas.omnia.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lucas.omnia.R
import com.lucas.omnia.adapters.SubscriptionsAdapter
import com.lucas.omnia.models.User
import java.util.*

class SubscriptionsActivity : BaseActivity() {
    private val context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subs)
        val toolbar = findViewById<Toolbar>(R.id.subs_tb)
        setSupportActionBar(toolbar)
        val noneTv = findViewById<TextView>(R.id.subs_tv_none)
        val subsQuery = databaseReference.child("users").child(uid).child("subs")
        val recyclerView: RecyclerView? = findViewById(R.id.subs_rv)
        recyclerView?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = layoutManager
        val userList: MutableList<User> = ArrayList()
        subsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = User(userSnapshot.key, userSnapshot.value.toString())
                    userList.add(user)
                }
                if (userList.isEmpty()) noneTv.visibility = View.VISIBLE else noneTv.visibility = View.GONE
                val recyclerAdapter = SubscriptionsAdapter(context,
                        userList)
                recyclerView?.adapter = recyclerAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getUsersInSubs:onCancelled", error.toException())
            }
        })
    }

    companion object {
        private const val TAG = "SubscriptionsActivity"
    }
}