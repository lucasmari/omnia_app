package com.lucas.omnia.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lucas.omnia.fragments.TabFragment1;
import com.lucas.omnia.fragments.TabFragment2;
import com.lucas.omnia.fragments.TabFragment3;
import com.lucas.omnia.fragments.TabFragment4;

/**
 * Created by Lucas on 10/10/2017.
 */

public class PagerAdapter1 extends FragmentStatePagerAdapter {

    int NumOfTabs;

    public PagerAdapter1(FragmentManager fm, int mNumOfTabs) {
        super(fm);

        this.NumOfTabs = mNumOfTabs;
    }

    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragment1 tabFragment1 = new TabFragment1();
                return tabFragment1;
            case 1:
                TabFragment2 tabFragment2 = new TabFragment2();
                return tabFragment2;
            case 2:
                TabFragment3 tabFragment3 = new TabFragment3();
                return tabFragment3;
            case 3:
                TabFragment4 tabFragment4 = new TabFragment4();
                return tabFragment4;
            default:
                return null;
        }
    }

    public int getCount() {
        return NumOfTabs;
    }
}
