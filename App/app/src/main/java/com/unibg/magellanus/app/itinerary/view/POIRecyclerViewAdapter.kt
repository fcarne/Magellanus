package com.unibg.magellanus.app.itinerary.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.unibg.magellanus.app.placeholder.PlaceholderContent.PlaceholderItem
import com.unibg.magellanus.app.databinding.FragmentPoiItemBinding


class POIRecyclerViewAdapter(
    private val values: List<com.unibg.magellanus.app.itinerary.model.POI>
) : RecyclerView.Adapter<POIRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentPoiItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.name.text = item.name
        holder.latitude.text = item.latitude.toString()
        holder.longitude.text = item.longitude.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPoiItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.name
        val latitude: TextView = binding.latitude
        val longitude: TextView = binding.longitude

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }
    }

}