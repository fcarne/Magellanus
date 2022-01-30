package com.unibg.magellanus.app.itinerary.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

import com.unibg.magellanus.app.databinding.FragmentItineraryItemBinding
import com.unibg.magellanus.app.itinerary.model.Itinerary


class ItineraryRecyclerViewAdapter(
    private val itemClickListener: OnItineraryItemClickListener,
    private val deleteClickListener: OnItineraryItemClickListener
) : RecyclerView.Adapter<ItineraryRecyclerViewAdapter.ViewHolder>() {

    private var values = mutableListOf<Itinerary>()

    fun setItineraries(itineraries: List<Itinerary>) {
        this.values = itineraries.toMutableList()
        notifyItemRangeChanged(0, itineraries.size)
    }

    fun addItinerary(itinerary: Itinerary) {
        this.values.add(itinerary)
        notifyItemInserted(values.lastIndex)
    }

    fun removeItinerary(index: Int) {
        this.values.removeAt(index)
        notifyItemRemoved(index)
    }

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
        val itinerary = values[position]
        holder.bind(itinerary, itemClickListener, deleteClickListener)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItineraryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.name
        private val date: TextView = binding.date
        private val deleteButton: ImageButton = binding.deleteBtn
        val view: View = binding.root

        fun bind(
            itinerary: Itinerary,
            itemClickListener: OnItineraryItemClickListener,
            deleteClickListener: OnItineraryItemClickListener
        ) {
            name.text = itinerary.name
            date.text = itinerary.completionDate?.toString()

            view.setOnClickListener {
                itemClickListener.onClick(itinerary)
            }

            deleteButton.setOnClickListener {
                deleteClickListener.onClick(itinerary)
            }
        }
    }

    fun interface OnItineraryItemClickListener {
        fun onClick(itinerary: Itinerary)
    }

}