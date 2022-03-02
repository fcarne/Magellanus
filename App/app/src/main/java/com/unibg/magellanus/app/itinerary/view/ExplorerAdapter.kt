package com.unibg.magellanus.app.itinerary.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

// adapter per il tabbed layout - segue il sample definito da Android Developer
internal class ExplorerAdapter(
    var totalTabs: Int,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
    override fun createFragment(position: Int): Fragment =
        ItineraryListFragment.newInstance(position)

    override fun getItemCount(): Int = totalTabs
}

