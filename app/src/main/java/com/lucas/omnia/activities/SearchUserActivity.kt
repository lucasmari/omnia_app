package com.lucas.omnia.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lucas.omnia.R
import com.lucas.omnia.adapters.SearchUserAdapter
import com.lucas.omnia.models.User
import java.util.*

class SearchUserActivity : BaseActivity() {
    private val context: Context = this
    private var usersReference: DatabaseReference? = null
    private var noneTv: TextView? = null
    private var recyclerView: RecyclerView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)
        noneTv = findViewById(R.id.search_user_tv_none)
        recyclerView = findViewById(R.id.search_results_rv)
        recyclerView?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = layoutManager
        usersReference = databaseReference.child("users")
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val queryString = intent.getStringExtra(SearchManager.QUERY)
            search(queryString)
        }
    }

    private fun search(queryString: String?) {
        // Set up FirebaseRecyclerAdapter with the Query
        val usersQuery = usersReference!!.orderByChild("username").startAt(queryString).endAt(queryString +
                "\uf8ff")
        val userList: MutableList<User> = ArrayList()
        usersQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = User(userSnapshot.key,
                            userSnapshot.child("username").value.toString())
                    userList.add(user)
                }
                val recyclerAdapter = SearchUserAdapter(context, userList)
                Log.e(TAG, recyclerAdapter.itemCount.toString())
                if (recyclerAdapter.itemCount == 0) noneTv!!.visibility = View.VISIBLE else
                    recyclerView!!.adapter = recyclerAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getUsersByUsername:onCancelled", error.toException())
            }
        })
    }

    companion object {
        private const val TAG = "SearchUserActivity"
    }
}