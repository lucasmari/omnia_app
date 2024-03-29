package com.lucas.omnia.adapters

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.lucas.omnia.fragments.*

class AppFragmentPageAdapter(fm: FragmentManager?, sp: SharedPreferences) : FragmentPagerAdapter(fm!!,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val sharedPreferences = sp

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FeedNavFragment()
            1 -> ProfileNavFragment(sharedPreferences)
            2 -> DiscussionNavFragment()
            3 -> InspectionNavFragment()
            4 -> ElectionsNavFragment()
            else -> throw RuntimeException("Not supported")
        }
    }

    override fun getCount(): Int {
        return 5
    }
}