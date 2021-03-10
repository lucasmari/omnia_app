package com.lucas.omnia.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.lucas.omnia.R
import com.lucas.omnia.models.User
import java.net.MalformedURLException

class UserPageActivity : BaseActivity() {
    private var userKey: String? = null
    private var userImgUrl: String? = null
    private var userImgView: ImageView? = null
    private var usernameTv: TextView? = null
    private var subCountTv: TextView? = null
    private var aboutTv: TextView? = null
    private var subButton: Button? = null
    private var userRef: DatabaseReference? = null
    var currentUserRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_page)
        userImgView = findViewById(R.id.user_page_iv)
        usernameTv = findViewById(R.id.user_page_tv_name)
        subCountTv = findViewById(R.id.user_page_tv_sub_count)
        aboutTv = findViewById(R.id.user_page_tv_about_body)

        // Get user key from intent
        userKey = intent.getStringExtra(EXTRA_USER_KEY)
        requireNotNull(userKey) { "Must pass EXTRA_USER_KEY" }
        userRef = databaseReference.child("users").child(userKey!!)
        currentUserRef = databaseReference.child("users").child(uid)
        setupUserPage()
        val postsButton = findViewById<Button>(R.id.user_page_bt_posts)
        if (userKey == uid) postsButton.visibility =
            View.GONE else postsButton.setOnClickListener { openUserPosts() }
        subButton = findViewById(R.id.user_page_bt_sub)
        if (userKey == uid) subButton!!.visibility = View.GONE else subButton!!.setOnClickListener { verifySub() }
    }

    private fun openUserPosts() {
        val intent = Intent(this, UserPostsActivity::class.java)
        intent.putExtra(EXTRA_USER_KEY, userKey)
        startActivity(intent)
    }

    private fun setupUserPage() {
        userRef!!.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val u = dataSnapshot.getValue(
                        User::class.java
                    )
                    if (u == null) {
                        Log.e(TAG, "User $userKey is unexpectedly null")
                        Toast.makeText(
                            this@UserPageActivity,
                            getString(R.string.new_post_toast_user_fetch_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        setUser(u)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "getUser:onCancelled", databaseError.toException())
                }
            })
        currentUserRef!!.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val u = dataSnapshot.getValue(
                        User::class.java
                    )
                    if (u == null) {
                        Log.e(TAG, "User $userKey is unexpectedly null")
                        Toast.makeText(
                            this@UserPageActivity,
                            getString(R.string.new_post_toast_user_fetch_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (u.subs.containsKey(userKey)) {
                            subButton!!.text = getString(R.string.user_page_bt_unsub)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "getUser:onCancelled", databaseError.toException())
                }
            })
    }

    private fun setUser(u: User) {
        usernameTv!!.text = u.username
        subCountTv!!.text = java.lang.String.valueOf(u.subCount)
        if (u.hasPhoto) fetchProfileImage()
        if (u.about != null) aboutTv!!.text = u.about
    }

    private fun fetchProfileImage() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val profileImgRef = storageRef.child(userKey + STORAGE_PATH)
        profileImgRef.downloadUrl.addOnSuccessListener { uri: Uri ->
            try {
                userImgUrl = uri.toString()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            userImgView?.load(userImgUrl) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }.addOnFailureListener {
            Toast.makeText(
                this@UserPageActivity, getString(R.string.profile_toast_fetch_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun verifySub() {
        currentUserRef!!.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val u = dataSnapshot.getValue(
                        User::class.java
                    )
                    if (u == null) {
                        Log.e(TAG, "User $userKey is unexpectedly null")
                        Toast.makeText(
                            this@UserPageActivity,
                            getString(R.string.new_post_toast_user_fetch_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (u.subs.containsKey(userKey)) {
                            unsetSub(u)
                        } else {
                            setSub(u)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "getUser:onCancelled", databaseError.toException())
                }
            })
    }

    private fun unsetSub(current: User) {
        userRef!!.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val u = mutableData.getValue(
                    User::class.java
                )
                    ?: return Transaction.success(mutableData)
                u.subCount = u.subCount - 1
                runOnUiThread {
                    current.subs.remove(userKey)
                    currentUserRef!!.child("subs").setValue(current.subs)
                    subButton!!.text = getString(R.string.user_page_bt_sub)
                    subCountTv!!.text = java.lang.String.valueOf(u.subCount)
                }

                // Set value and report transaction success
                mutableData.value = u
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?, committed: Boolean,
                currentData: DataSnapshot?
            ) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:$databaseError")
            }
        })
        Toast.makeText(this, getString(R.string.user_page_unsub), Toast.LENGTH_SHORT).show()
    }

    private fun setSub(current: User) {
        userRef!!.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val u = mutableData.getValue(
                    User::class.java
                )
                    ?: return Transaction.success(mutableData)
                u.subCount = u.subCount + 1
                runOnUiThread {
                    current.subs[u.uid.toString()] = u.username.toString()
                    currentUserRef!!.child("subs").setValue(current.subs)
                    subButton!!.text = getString(R.string.user_page_bt_unsub)
                    subCountTv!!.text = java.lang.String.valueOf(u.subCount)
                }

                // Set value and report transaction success
                mutableData.value = u
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?, committed: Boolean,
                currentData: DataSnapshot?
            ) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:$databaseError")
            }
        })
        Toast.makeText(this, getString(R.string.user_page_sub), Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_USER_KEY = "user_key"
        private const val TAG = "UserPageActivity"
        private const val STORAGE_PATH = "/profile-picture/profile.jpg"
    }
}