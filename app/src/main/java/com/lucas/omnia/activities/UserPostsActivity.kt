package com.lucas.omnia.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.lucas.omnia.R
import com.lucas.omnia.fragments.UserPostsTabFragment

class UserPostsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_posts)
        val toolbar = findViewById<Toolbar>(R.id.user_posts_tb)
        setSupportActionBar(toolbar)

        // Get user key from intent
        val userKey = intent.getStringExtra(EXTRA_USER_KEY)
                ?: throw IllegalArgumentException("Must pass EXTRA_USER_KEY")
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.user_posts_fcv, UserPostsTabFragment(userKey))
                .commit()
    }

    companion object {
        const val EXTRA_USER_KEY = "user_key"
    }
}