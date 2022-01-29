package com.unibg.magellanus.app.itinerary.view

import ExplorerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayout
import com.unibg.magellanus.app.R
import com.unibg.magellanus.app.databinding.FragmentItineraryExplorerBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ItineraryExplorerFragment : Fragment() {
    private lateinit var binding: FragmentItineraryExplorerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentItineraryExplorerBinding.inflate(inflater, container, false)
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Todo"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("History"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = ExplorerAdapter(this.requireContext(),childFragmentManager,binding.tabLayout.tabCount)
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return inflater.inflate(R.layout.fragment_itinerary_explorer, container, false)
    }

}