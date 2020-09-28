package com.lucas.omnia.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.lucas.omnia.R;
import com.lucas.omnia.activities.NewPostActivity;

/**
 * Created by Lucas on 10/10/2017.
 */

public class NavFragment1 extends Fragment{

    static public FloatingActionButton addFab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.fragment_nav_1, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.pager);
        GooglePlusFragmentPageAdapter adapter =
                new GooglePlusFragmentPageAdapter(
                        getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        addFab = view.findViewById(R.id.addFab);
        addFab.setOnClickListener(v -> {
            startActivity(new Intent(view.getContext(), NewPostActivity.class));
        });
    }

    private class GooglePlusFragmentPageAdapter extends FragmentPagerAdapter {

        public GooglePlusFragmentPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        private final Fragment[] mFragments = new Fragment[]{
                new TabFragment1(),
                new TabFragment2(),
                new TabFragment3(),
                new TabFragment4()
        };

        private final String[] fragmentNames = new String[]{
                getString(R.string.tabFragment1),
                getString(R.string.tabFragment2),
                getString(R.string.tabFragment3),
                getString(R.string.tabFragment4)
        };

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentNames[position];
        }
    }
}
