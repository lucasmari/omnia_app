package com.lucas.omnia.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lucas.omnia.AppFragmentPageAdapter;
import com.lucas.omnia.BottomNavItemSelectedListener;
import com.lucas.omnia.R;
import com.lucas.omnia.fragments.NavFragment1;

/**
 * Created by Lucas on 29/10/2017.
 */

public class MainActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private BottomNavigationView navigation;
    private Toolbar toolbar;

    public static final String KEY_FRAGMENT = "fragment";
    private boolean doubleBackToExitPressedOnce = false;

    public static String userName;

    /*private SparseArray<Fragment.SavedState> savedStateSparseArray = new SparseArray();
    private int currentSelectedItemId = R.id.action_item1;
    private String SAVED_STATE_CONTAINER_KEY = "ContainerKey";
    private String  SAVED_STATE_CURRENT_TAB_KEY = "CurrentTabKey";*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*setupSharedPreferences();*/

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager viewPager = findViewById(R.id.container);
        AppFragmentPageAdapter adapter = new AppFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        navigation = findViewById(R.id.navigation);
        BottomNavItemSelectedListener listener = new BottomNavItemSelectedListener(viewPager, toolbar);
        navigation.setOnNavigationItemSelectedListener(listener);

        /*if (savedInstanceState != null) {
            savedStateSparseArray = savedInstanceState.getSparseParcelableArray(SAVED_STATE_CONTAINER_KEY);
            currentSelectedItemId = savedInstanceState.getInt(SAVED_STATE_CURRENT_TAB_KEY);
        } else
            swapFragments( new NavFragment1(), currentSelectedItemId, KEY_FRAGMENT);*/

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

        /*CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams2.setBehavior(new BottomNavigationViewBehavior());*/

        /*navigation.setOnNavigationItemSelectedListener
                (item -> {
                    switch (item.getItemId()) {
                        case R.id.action_item1:
                            swapFragments(new NavFragment1(), item.getItemId(), "nav 1");
                            break;
                        case R.id.action_item2:
                            swapFragments(new NavFragment2(), item.getItemId(), "nav 2");
                            break;
                        case R.id.action_item3:
                            swapFragments(new NavFragment3(), item.getItemId(), "nav 3");
                            break;
                        case R.id.action_item4:
                            swapFragments(new NavFragment4(), item.getItemId(), "nav 4");
                            break;
                        case R.id.action_item5:
                            swapFragments(new NavFragment5(), item.getItemId(), "nav 5");
                            break;
                    }
                    return true;
                });
        navigation.setSelectedItemId(R.id.action_item1);*/
    }

    /*@Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSparseParcelableArray(SAVED_STATE_CONTAINER_KEY, savedStateSparseArray);
        outState.putInt(SAVED_STATE_CURRENT_TAB_KEY, currentSelectedItemId);
    }

    private void initFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


    private void saveFragmentState(int actionId) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment != null) {
            savedStateSparseArray.put(currentSelectedItemId, getSupportFragmentManager().saveFragmentInstanceState(currentFragment));
        }

        currentSelectedItemId = actionId;
    }


    private void swapFragments(Fragment fragment, int actionId, String key) {
        if (getSupportFragmentManager().findFragmentByTag(key) == null) {
            saveFragmentState(actionId);
            initFragment(fragment);
        }
    }*/

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
        Toast.makeText(this, "Pressione novamente para sair", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
