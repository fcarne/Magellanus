package com.unibg.magellanus.app.itinerary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unibg.magellanus.app.R
import com.unibg.magellanus.app.common.observeOnce
import com.unibg.magellanus.app.itinerary.model.ItineraryRepositoryImpl
import com.unibg.magellanus.app.itinerary.model.network.GeocodingAPI
import com.unibg.magellanus.app.itinerary.model.network.ItineraryAPI
import com.unibg.magellanus.app.itinerary.viewmodel.POIListViewModel
import com.unibg.magellanus.app.user.auth.impl.FirebaseAuthenticationProvider

class POIListFragment : Fragment() {

    private var columnCount = 1

    private lateinit var itineraryId: String

    private val viewModel by viewModels<POIListViewModel> {
        itineraryId = args.itineraryId

        val provider = FirebaseAuthenticationProvider
        val cacheDir = requireContext().cacheDir
        val api = ItineraryAPI.create(provider, cacheDir)
        val geoApi = GeocodingAPI.create(cacheDir)
        val repository = ItineraryRepositoryImpl(api, geoApi)

        POIListViewModel.Factory(itineraryId, repository)
    }

    private lateinit var navController: NavController
    private val args: POIListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_poi_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }


                viewModel.poiList.observeOnce(viewLifecycleOwner) {
                    val locateClickListener =
                        POIRecyclerViewAdapter.OnPOIItemClickListener { poi ->
                            navController.navigate(
                                POIListFragmentDirections.actionPOIListFragmentToMapFragment(
                                    itineraryId,
                                    floatArrayOf(poi.lat.toFloat(), poi.lon.toFloat())
                                )
                            )
                        }

                    adapter = POIRecyclerViewAdapter(locateClickListener).apply {
                        setPOIList(it)
                    }
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }
}