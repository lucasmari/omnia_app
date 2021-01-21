package com.lucas.omnia.activities

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.preference.PreferenceManager
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lucas.omnia.R

open class BaseActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {
    private var progressBar: ProgressBar? = null
    fun setProgressBar(resId: Int) {
        progressBar = findViewById(resId)
    }

    fun showProgressBar() {
        if (progressBar != null) {
            progressBar!!.visibility = View.VISIBLE
        }
    }

    fun hideProgressBar() {
        if (progressBar != null) {
            progressBar!!.visibility = View.GONE
        }
    }

    val databaseReference: DatabaseReference
        get() = FirebaseDatabase.getInstance().reference
    val uid: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid

    open fun setupSharedPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    public override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.settings_theme_pref_key)) {
            switchTheme(sharedPreferences.getBoolean(key, false))
            recreate()
        }
    }

    override fun setTheme(resid: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = sharedPreferences.getBoolean(getString(R.string.settings_theme_pref_key), false)
        if (theme) {
            super.setTheme(R.style.AppThemeLight)
        } else {
            super.setTheme(R.style.AppThemeDark)
        }
    }

    private fun switchTheme(b: Boolean) {
        if (b) {
            this.setTheme(R.style.AppThemeLight)
        } else {
            this.setTheme(R.style.AppThemeDark)
        }
    }
}