package com.unibg.magellanus.app.itinerary.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unibg.magellanus.app.databinding.FragmentPoiItemBinding
import com.unibg.magellanus.app.itinerary.model.POI

class POIRecyclerViewAdapter(
    private val locateClickListener: OnPOIItemClickListener,
    private val checkboxCheckedChangeListener: OnCheckboxCheckedChangeListener,
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
        holder.bind(poi, position, locateClickListener, checkboxCheckedChangeListener)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPoiItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.name
        private val address = binding.address
        private val locateButton = binding.locateBtn
        private val selectedCheckBox = binding.selectCheckbox

        fun bind(
            poi: POI,
            position: Int,
            itemClickListener: OnPOIItemClickListener,
            checkboxCheckedChangeListener: OnCheckboxCheckedChangeListener,
        ) {
            name.text = poi.name
            address.text = poi.address?.formatted
            selectedCheckBox.isChecked = poi.inRoute

            locateButton.setOnClickListener {
                itemClickListener.onClick(poi)
            }

            selectedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                checkboxCheckedChangeListener.onCheckedChange(poi, isChecked)
                this.bindingAdapter?.notifyItemChanged(position)
            }
        }
    }

    // custom click listener
    fun interface OnPOIItemClickListener {
        fun onClick(poi: POI)
    }

    // custom checkbox checked listener
    fun interface OnCheckboxCheckedChangeListener {
        fun onCheckedChange(poi: POI, isChecked: Boolean)
    }

}