package com.lucas.omnia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.lucas.omnia.R

class FeedNavFragment : Fragment() {
    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_feed, viewGroup, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.feed_title)
        val viewPager: ViewPager = view.findViewById(R.id.feed_vp)
        val adapter = GooglePlusFragmentPageAdapter(childFragmentManager)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count - 1
        val tabLayout: TabLayout = view.findViewById(R.id.feed_tl)
        tabLayout.setupWithViewPager(viewPager)
        val addFab: FloatingActionButton? = view.findViewById(R.id.feed_fab_add)
        addFab?.setOnClickListener {
            val addPostBottomDialogFragment = PostBottomSheetDialogFragment.newInstance()
            addPostBottomDialogFragment.show(parentFragmentManager, POST_TAG)
        }
    }

    private inner class GooglePlusFragmentPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragments = arrayOf<Fragment>(
                HotPostsTabFragment(),
                RecentPostsTabFragment(),
                MyPostsTabFragment(),
                UpVotedPostsTabFragment()
        )
        private val fragmentNames = arrayOf(
                getString(R.string.feed_tab_1),
                getString(R.string.feed_tab_2),
                getString(R.string.feed_tab_3),
                getString(R.string.feed_tab_4)
        )

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentNames[position]
        }
    }

    companion object {
        @JvmField
        var addFab: FloatingActionButton? = null
        private const val POST_TAG = "add_post_dialog_fragment"
    }
}