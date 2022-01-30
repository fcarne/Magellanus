package com.unibg.magellanus.app.itinerary.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.unibg.magellanus.app.BuildConfig
import com.unibg.magellanus.app.R
import com.unibg.magellanus.app.databinding.FragmentMapBinding
import com.unibg.magellanus.app.itinerary.model.ItineraryRepositoryImpl
import com.unibg.magellanus.app.itinerary.model.POI
import com.unibg.magellanus.app.itinerary.model.network.GeocodingAPI
import com.unibg.magellanus.app.itinerary.model.network.ItineraryAPI
import com.unibg.magellanus.app.itinerary.viewmodel.MapViewModel
import com.unibg.magellanus.app.user.auth.impl.FirebaseAuthenticationProvider
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment() {

    private val provider = FirebaseAuthenticationProvider

    private lateinit var itineraryId: String
    private var startingCoordinates: GeoPoint? = null

    private val viewModel by viewModels<MapViewModel> {
        itineraryId = args.itineraryId ?: provider.currentUser!!.uid
        startingCoordinates = args.coordinates?.let {
            GeoPoint(it[0].toDouble(), it[1].toDouble())
        }

        val cacheDir = requireContext().cacheDir
        val api = ItineraryAPI.create(provider, cacheDir)
        val geoApi = GeocodingAPI.create(cacheDir)
        val repository = ItineraryRepositoryImpl(api, geoApi)

        MapViewModel.Factory(itineraryId, repository)
    }

    private lateinit var navController: NavController
    private val args: MapFragmentArgs by navArgs()

    private lateinit var binding: FragmentMapBinding
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    private lateinit var map: MapView
    private lateinit var mapController: IMapController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.bntCentra.setOnClickListener { btnCentra() }
        binding.btnPOI.setOnClickListener { btnPOI() }
        binding.btnItinerari.setOnClickListener { btnItinerari() }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                if (s.length > 2)
                    viewModel.search(s)
                binding.searchBar.clearFocus()
                return false
            }

            override fun onQueryTextChange(s: String): Boolean = false
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        navController = findNavController()

        val ctx = requireActivity().applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.visibility = View.VISIBLE

        if (provider.currentUser == null) {
            navController.navigate(MapFragmentDirections.actionMapFragmentToLoginFragment())
            return
        } else {

            //Permissions check
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                if (ContextCompat.checkSelfPermission(
                        this.requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_DENIED
                )
                    ActivityCompat.requestPermissions(
                        this.requireActivity(),
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        1
                    )
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_DENIED
            )
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(
                        Manifest.permission.INTERNET
                    ),
                    1
                )

            //Controller usage, overlay declaration and location enabling
            this.mapController = map.controller

            val arrowIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_navigation_24)
                    ?.toBitmap()

            myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map).apply {
                enableMyLocation()
                enableFollowLocation()
                isDrawAccuracyEnabled = true
                setDirectionArrow(arrowIcon, arrowIcon)
                setPersonIcon(arrowIcon)
            }

            this.mapController.setZoom(18.0)
            val searchOverlay = FolderOverlay()
            val poiOverlay = FolderOverlay()

            map.overlays.apply {
                add(myLocationOverlay)
                add(searchOverlay)
                add(poiOverlay)
            }


            val eventsReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                    return false
                }

                override fun longPressHelper(p: GeoPoint): Boolean {
                    val poi = viewModel.searchPOI(p.latitude, p.longitude) ?: POI(
                        lat = p.latitude,
                        lon = p.longitude
                    )

                    viewModel.addPOI(poi)
                    return true
                }
            }

            val eventOverlay = MapEventsOverlay(eventsReceiver)
            map.overlays.add(eventOverlay)

            map.invalidate()

            startingCoordinates?.also {
                mapController.animateTo(it)
            }

            viewModel.poiSet.observe(viewLifecycleOwner) {
                poiOverlay.items.clear()
                it.forEach { poi ->
                    val marker = Marker(map)
                    marker.position = GeoPoint(poi.lat, poi.lon)
                    marker.title = poi.name
                    marker.icon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_place_24,
                        null
                    )
                    marker.setOnMarkerClickListener { _, _ ->
                        viewModel.removePOI(poi)
                        Toast.makeText(requireContext(), "Removed POI!", Toast.LENGTH_LONG).show()
                        true
                    }
                    poiOverlay.add(marker)
                }
            }

            viewModel.currentSearch.observe(viewLifecycleOwner) {
                searchOverlay.items.clear()
                it?.forEach { poi ->
                    val marker = Marker(map)
                    marker.position = GeoPoint(poi.lat, poi.lon)
                    marker.title = poi.name
                    marker.icon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_add_location_alt_24,
                        null
                    )
                    marker.setOnMarkerClickListener { _, _ ->
                        viewModel.addPOI(poi)
                        Toast.makeText(requireContext(), "Added POI!", Toast.LENGTH_LONG).show()
                        true
                    }
                    searchOverlay.add(marker)
                }

                mapController.animateTo((searchOverlay.items[0] as Marker).position)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }

    fun btnCentra() {
        this.mapController.animateTo(myLocationOverlay.myLocation)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.isDrawAccuracyEnabled = true
    }

    fun btnPOI() {
        navController.navigate(MapFragmentDirections.actionMapFragmentToPOIFragment(itineraryId))
    }

    fun btnItinerari() {
        navController.navigate(MapFragmentDirections.actionMapFragmentToItineraryExplorerFragment())
    }

    override fun onStop() {
        super.onStop()
        requireActivity().lifecycleScope.launch {
            viewModel.saveChanges()
        }
    }
}