package com.lucas.omnia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.lucas.omnia.R;

/**
 * Created by Lucas on 10/10/2017.
 */

public class FeedNavFragment extends Fragment{

    public static FloatingActionButton addFab;
    private static final String POST_TAG = "add_post_dialog_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.fragment_feed, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.feed_title);

        ViewPager viewPager = view.findViewById(R.id.feed_vp);
        GooglePlusFragmentPageAdapter adapter = new GooglePlusFragmentPageAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        TabLayout tabLayout = view.findViewById(R.id.feed_tl);
        tabLayout.setupWithViewPager(viewPager);

        addFab = view.findViewById(R.id.feed_fab_add);
        addFab.setOnClickListener(v -> {
            PostBottomSheetDialogFragment addPostBottomDialogFragment =
                    PostBottomSheetDialogFragment.newInstance();
            addPostBottomDialogFragment.show(getParentFragmentManager(), POST_TAG);
        });
    }

    private class GooglePlusFragmentPageAdapter extends FragmentPagerAdapter {

        public GooglePlusFragmentPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        private final Fragment[] fragments = new Fragment[]{
                new HotPostsTabFragment(),
                new RecentPostsTabFragment(),
                new MyPostsTabFragment(),
                new UpVotedPostsTabFragment()
        };

        private final String[] fragmentNames = new String[]{
                getString(R.string.feed_tab_1),
                getString(R.string.feed_tab_2),
                getString(R.string.feed_tab_3),
                getString(R.string.feed_tab_4)
        };

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentNames[position];
        }
    }
}
