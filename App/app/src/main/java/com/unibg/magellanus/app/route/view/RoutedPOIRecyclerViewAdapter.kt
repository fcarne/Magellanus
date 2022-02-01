package com.unibg.magellanus.app.route.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.unibg.magellanus.app.R
import com.unibg.magellanus.app.databinding.FragmentRoutedPoiItemBinding
import com.unibg.magellanus.app.route.model.RoutedPOI
import kotlin.math.abs
import kotlin.math.roundToInt

class RoutedPOIRecyclerViewAdapter(
    private val deleteClickListener: OnItineraryItemClickListener
) : RecyclerView.Adapter<RoutedPOIRecyclerViewAdapter.ViewHolder>() {

    private var values = mutableListOf<RoutedPOI>()

    fun setRoute(route: List<RoutedPOI>) {
        this.values = route.toMutableList()
        notifyItemRangeChanged(0, route.size)
    }

    fun moveRoutedPOI(from: Int, to: Int) {
        val poi = values[from]
        values.removeAt(from)
        if (to < from) {
            values.add(to, poi)
        } else {
            values.add(to - 1, poi)
        }
        notifyItemMoved(from, to)
        notifyItemRangeChanged(from - 1, abs(from-to))
    }

    fun removeRoutedPOI(index: Int) {
        this.values.removeAt(index)
        notifyItemRemoved(index)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentRoutedPoiItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val routedPOI = values[position]
        holder.bind(routedPOI, deleteClickListener)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentRoutedPoiItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val name = binding.name
        private val distance = binding.distance
        private val deleteButton: ImageButton = binding.deleteBtn
        private val context = binding.root.context

        fun bind(
            routedPOI: RoutedPOI,
            deleteClickListener: OnItineraryItemClickListener
        ) {
            name.text = routedPOI.name
            distance.text =
                context.getString(R.string.distance_string, routedPOI.distance.roundToInt())
            deleteButton.setOnClickListener {
                deleteClickListener.onClick(routedPOI)
            }
        }
    }

    fun interface OnItineraryItemClickListener {
        fun onClick(routedPOI: RoutedPOI)
    }


}