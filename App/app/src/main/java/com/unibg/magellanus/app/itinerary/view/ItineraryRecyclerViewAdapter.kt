package com.unibg.magellanus.app.itinerary.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.unibg.magellanus.app.placeholder.PlaceholderContent.PlaceholderItem
import com.unibg.magellanus.app.databinding.FragmentItineraryItemBinding
import com.unibg.magellanus.app.itinerary.model.Itinerary


class ItineraryRecyclerViewAdapter(
    private val values: List<Itinerary>
) : RecyclerView.Adapter<ItineraryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItineraryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.id.text = item.id
        holder.name.text = item.name
        holder.date.text = item.date
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItineraryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val id: TextView = binding.id
        val name: TextView = binding.name
        val date: TextView = binding.date

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }
    }

}