package com.rbmstroy.rbmbonus.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rbmstroy.rbmbonus.tabs.CardTab
import com.rbmstroy.rbmbonus.tabs.EventTab

class MyPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CardTab()
            else -> EventTab()
        }
    }
}