package com.lucas.omnia.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucas.omnia.R;
import com.lucas.omnia.adapters.AppFragmentPageAdapter;
import com.lucas.omnia.utils.BottomNavItemSelectedListener;

/**
 * Created by Lucas on 29/10/2017.
 */

public class MainActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private boolean doubleBackToExitPressedOnce = false;

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*setupSharedPreferences();*/

        Toolbar toolbar = findViewById(R.id.main_tb);
        setSupportActionBar(toolbar);
        ViewPager viewPager = findViewById(R.id.main_cvp);
        AppFragmentPageAdapter adapter = new AppFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        BottomNavigationView navigation = findViewById(R.id.main_bnv);
        BottomNavItemSelectedListener listener = new BottomNavItemSelectedListener(viewPager, toolbar);
        navigation.setOnNavigationItemSelectedListener(listener);
    }

    public void setTheme(boolean b) {
        /*if(b) {
            this.setTheme(R.style.AppThemeLight);
            navigation.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            navigation.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.selector_light)));
            navigation.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.selector_light)));
        }
        else {
            this.setTheme(R.style.AppThemeDark);
            navigation.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            navigation.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.selector)));
            navigation.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.selector)));
        }*/
    }

    public void setupSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        setTheme(sharedPreferences.getBoolean(getString(R.string.theme_pref_key), false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_1, menu);

        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.theme_pref_key))) {
            setTheme(sharedPreferences.getBoolean(key, false));
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.press_back_to_leave), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
