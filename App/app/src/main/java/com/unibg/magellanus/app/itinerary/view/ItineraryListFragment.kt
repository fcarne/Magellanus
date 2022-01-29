package com.unibg.magellanus.app.itinerary.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unibg.magellanus.app.R
import com.unibg.magellanus.app.itinerary.model.Itinerary
import com.unibg.magellanus.app.itinerary.model.POI
import com.unibg.magellanus.app.placeholder.PlaceholderContent
import java.sql.Date

/**
 * A fragment representing a list of Items.
 */
class ItineraryListFragment : Fragment() {

    private var columnCount = 1
    private var call = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_itinerary_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                //Lista temporanea di test

                //Separazione dei casi in cui sia to do oppure storico
                if(call == 0)
                {
                    var effe = listOf<Itinerary>(
                        Itinerary("01","Primo viaggio", "01/01/2001"),
                        Itinerary("02","Secondo viaggio", "01/01/2002"),
                        Itinerary("03","Terzo viaggio", "01/01/2003"),
                    )
                    adapter = ItineraryRecyclerViewAdapter(effe)
                }else{
                    var effe = listOf<Itinerary>(
                        Itinerary("04","Gia fatto 1", "01/01/2001"),
                        Itinerary("05","Gia fatto 2", "01/01/2002"),
                        Itinerary("06","Gia fatto 3", "01/01/2003"),
                    )
                    adapter = ItineraryRecyclerViewAdapter(effe)
                }
            }
        }
        return view
    }

    companion object {
        //Gestisco la chiamata per lo storico o la lista dei to do con la variabile call, 0=to do , 1=storico
        @JvmStatic
        fun newInstance(call: Int): ItineraryListFragment {
            val fragment = ItineraryListFragment()
            val args = Bundle()
            args.putInt("call", call)
            fragment.setArguments(args)
            return fragment
        }
    }

}