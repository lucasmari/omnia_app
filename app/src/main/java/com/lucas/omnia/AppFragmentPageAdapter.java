package com.lucas.omnia;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lucas.omnia.fragments.NavFragment1;
import com.lucas.omnia.fragments.NavFragment2;
import com.lucas.omnia.fragments.NavFragment3;
import com.lucas.omnia.fragments.NavFragment4;
import com.lucas.omnia.fragments.NavFragment5;

public class AppFragmentPageAdapter extends FragmentPagerAdapter {

    public AppFragmentPageAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NavFragment1();
            case 1:
                return new NavFragment2();
            case 2:
                return new NavFragment3();
            case 3:
                return new NavFragment4();
            case 4:
                return new NavFragment5();
            default:
                throw new RuntimeException("Not supported");
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
