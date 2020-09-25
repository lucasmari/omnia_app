package com.lucas.omnia.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lucas.omnia.R;
import com.lucas.omnia.activities.NewPostActivity;
import com.lucas.omnia.databinding.FragmentNav1Binding;

/**
 * Created by Lucas on 10/10/2017.
 */

public class NavFragment1 extends Fragment{

    static public FloatingActionButton addFab;

    public static NavFragment1 newInstance() { return new NavFragment1(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        FragmentNav1Binding binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_nav_1, viewGroup, false);
        View view = binding.getRoot();

        // Create the adapter that will return a fragment for each section
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getParentFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final Fragment[] mFragments = new Fragment[]{
                    new TabFragment1(),
                    new TabFragment2(),
                    new TabFragment3(),
                    new TabFragment4()
            };
            private final String[] mFragmentNames = new String[]{
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

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };

        // Set up the ViewPager with the sections adapter.
        binding.pager.setAdapter(mPagerAdapter);
        binding.tabs.setupWithViewPager(binding.pager);

        addFab = view.findViewById(R.id.addFab);
        addFab.setOnClickListener(v -> {
                startActivity(new Intent(view.getContext(), NewPostActivity.class));
        });
        return view;
    }
}
