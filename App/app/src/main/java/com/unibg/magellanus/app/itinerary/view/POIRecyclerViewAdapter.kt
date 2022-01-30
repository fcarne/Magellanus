package com.unibg.magellanus.app.itinerary.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unibg.magellanus.app.databinding.FragmentPoiItemBinding
import com.unibg.magellanus.app.itinerary.model.POI

class POIRecyclerViewAdapter(
    private val locateClickListener: OnPOIItemClickListener
) : RecyclerView.Adapter<POIRecyclerViewAdapter.ViewHolder>() {

    private var values = mutableListOf<POI>()

    fun setPOIList(poiList: List<POI>) {
        this.values = poiList.toMutableList()
        notifyItemRangeChanged(0, poiList.size)
    }

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
        val poi = values[position]
        holder.bind(poi, locateClickListener)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPoiItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.name
        private val address: TextView = binding.address
        private val locateButton: ImageButton = binding.locateBtn

        fun bind(
            poi: POI,
            itemClickListener: OnPOIItemClickListener,
        ) {
            name.text = poi.name
            address.text = poi.address?.formatted

            locateButton.setOnClickListener {
                itemClickListener.onClick(poi)
            }
        }
    }

    fun interface OnPOIItemClickListener {
        fun onClick(poi: POI)
    }

}