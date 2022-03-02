package com.unibg.magellanus.app.itinerary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.unibg.magellanus.app.databinding.FragmentItineraryExplorerBinding

class ItineraryExplorerFragment : Fragment() {
    private lateinit var binding: FragmentItineraryExplorerBinding

    private val tabTitles = arrayOf("Todo", "History")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentItineraryExplorerBinding.inflate(inflater, container, false)
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ExplorerAdapter(tabTitles.size, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        // setta il gestore dei tab
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}