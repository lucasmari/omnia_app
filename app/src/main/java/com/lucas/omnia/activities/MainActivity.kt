package com.lucas.omnia.activities

import android.app.SearchManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.lucas.omnia.R
import com.lucas.omnia.adapters.AppFragmentPageAdapter
import com.lucas.omnia.utils.BottomNavItemSelectedListener

class MainActivity : BaseActivity(), OnSharedPreferenceChangeListener {
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupSharedPreferences()
        val toolbar = findViewById<Toolbar>(R.id.main_tb)
        setSupportActionBar(toolbar)
        val viewPager = findViewById<ViewPager>(R.id.main_cvp)
        val adapter = AppFragmentPageAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count - 1
        val navigation = findViewById<BottomNavigationView>(R.id.main_bnv)
        val listener = BottomNavItemSelectedListener(viewPager, toolbar)
        navigation.setOnNavigationItemSelectedListener(listener)
        fetchUser()
    }

    /*private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }*/
    private fun fetchUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Check if user's email is verified
            val emailVerified = user.isEmailVerified
            if (!emailVerified) Toast.makeText(this,
                    getString(R.string.main_email_toast_verification), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this, SearchUserActivity::class.java)))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_laws -> {
                intent = Intent(this, SearchLawActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.main_toast_press_back), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    companion object {
        const val RESULT_OK = -1
        const val TAG = "MainActivity"
    }
}