package com.lucas.omnia.activities

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.lucas.omnia.R

class SettingsActivity : BaseActivity(), OnSharedPreferenceChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupSharedPreferences()
        val toolbar = findViewById<Toolbar>(R.id.settings_tb)
        setSupportActionBar(toolbar)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_fr, SettingsFragment())
                .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
            setPreferencesFromResource(R.xml.preference, rootKey)
        }
    }

    override fun setupSharedPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
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

    private fun switchTheme(b: Boolean) {
        if (b) {
            this.setTheme(R.style.AppThemeLight)
        } else {
            this.setTheme(R.style.AppThemeDark)
        }
    }
}