package com.lucas.omnia.fragments;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.lucas.omnia.R;
import com.lucas.omnia.adapters.PagerAdapter1;

/**
 * Created by Lucas on 10/10/2017.
 */

public class NavFragment1 extends Fragment{

    public static FloatingActionButton mAddFab;
    public static FloatingActionButton mAttachFab;
    public static FloatingActionButton mCameraFab;
    public static RelativeLayout mRelativeLayout;
    public static EditText mEditText;
    public static ImageButton mSendButton;

    public static NavFragment1 newInstance() { return new NavFragment1(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_1, container, false);

        TabLayout mTabLayout = view.findViewById(R.id.tabLayout);

        mTabLayout.addTab(mTabLayout.newTab().setText("TabFragment1"));
        mTabLayout.addTab(mTabLayout.newTab().setText("TabFragment2"));
        mTabLayout.addTab(mTabLayout.newTab().setText("TabFragment3"));
        mTabLayout.addTab(mTabLayout.newTab().setText("TabFragment4"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = view.findViewById(R.id.pager1);
        final PagerAdapter1 adapter = new PagerAdapter1
                (getFragmentManager(), mTabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mAddFab = view.findViewById(R.id.addFab);
        mAttachFab = view.findViewById(R.id.attachFab);
        mCameraFab = view.findViewById(R.id.cameraFab);

        mRelativeLayout = view.findViewById(R.id.relativeLayout);
        mEditText = view.findViewById(R.id.editText);
        mSendButton = view.findViewById(R.id.sendButton);

        return view;
    }
}
