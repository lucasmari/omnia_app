package com.lucas.omnia.utils

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lucas.omnia.R

class BottomNavItemSelectedListener(private val viewPager: ViewPager, private val toolbar: Toolbar) : BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        toolbar.title = item.title
        val itemId = item.itemId
        if (itemId == R.id.action_item1) {
            viewPager.currentItem = 0
            return true
        } else if (itemId == R.id.action_item2) {
            viewPager.currentItem = 1
            return true
        } else if (itemId == R.id.action_item3) {
            viewPager.currentItem = 2
            return true
        } else if (itemId == R.id.action_item4) {
            viewPager.currentItem = 3
            return true
        } else if (itemId == R.id.action_item5) {
            viewPager.currentItem = 4
            return true
        }
        return false
    }
}