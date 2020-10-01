package com.lucas.omnia.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lucas.omnia.fragments.DiscussionNavFragment;
import com.lucas.omnia.fragments.ElectionsNavFragment;
import com.lucas.omnia.fragments.FeedNavFragment;
import com.lucas.omnia.fragments.ProfileNavFragment;
import com.lucas.omnia.fragments.ProjectsNavFragment;

public class AppFragmentPageAdapter extends FragmentPagerAdapter {

    public AppFragmentPageAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FeedNavFragment();
            case 1:
                return new ProfileNavFragment();
            case 2:
                return new DiscussionNavFragment();
            case 3:
                return new ProjectsNavFragment();
            case 4:
                return new ElectionsNavFragment();
            default:
                throw new RuntimeException("Not supported");
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
