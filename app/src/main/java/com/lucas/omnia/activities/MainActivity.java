package com.lucas.omnia.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lucas.omnia.BottomNavigationViewBehavior;
import com.lucas.omnia.R;
import com.lucas.omnia.fragments.NavFragment2;
import com.lucas.omnia.fragments.NavFragment3;
import com.lucas.omnia.fragments.NavFragment4;
import com.lucas.omnia.fragments.NavFragment5;

import static com.lucas.omnia.fragments.NavFragment1.newInstance;

/**
 * Created by Lucas on 29/10/2017.
 */

public class MainActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private BottomNavigationView mBottomNavigationView;
    private boolean doubleBackToExitPressedOnce = false;

    public static String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSharedPreferences();

        // Fetching user details
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            userName = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

        mBottomNavigationView = findViewById(R.id.navigation);

        CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) mBottomNavigationView.getLayoutParams();
        layoutParams2.setBehavior(new BottomNavigationViewBehavior());

        mBottomNavigationView.setOnNavigationItemSelectedListener
                (item -> {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.action_item1:
                            selectedFragment = newInstance();
                            break;
                        case R.id.action_item2:
                            selectedFragment = NavFragment2.newInstance();
                            break;
                        case R.id.action_item3:
                            selectedFragment = NavFragment3.newInstance();
                            break;
                        case R.id.action_item4:
                            selectedFragment = NavFragment4.newInstance();
                            break;
                        case R.id.action_item5:
                            selectedFragment = NavFragment5.newInstance();
                            break;
                    }
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    assert selectedFragment != null;
                    transaction.replace(R.id.coordinator_layout, selectedFragment);
                    transaction.commit();
                    return true;
                });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.coordinator_layout, newInstance());
        transaction.commit();
    }

    public void setTheme(boolean b) {
        mBottomNavigationView = findViewById(R.id.navigation);

        if(b) {
            this.setTheme(R.style.AppThemeLight);
            mBottomNavigationView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            mBottomNavigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.selector_light)));
            mBottomNavigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.selector_light)));
        }
        else {
            this.setTheme(R.style.AppThemeDark);
            mBottomNavigationView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mBottomNavigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.selector)));
            mBottomNavigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.selector)));
        }
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

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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
        Toast.makeText(this, "Pressione novamente para sair", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
