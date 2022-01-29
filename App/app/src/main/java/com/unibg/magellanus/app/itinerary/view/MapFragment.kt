package com.unibg.magellanus.app.itinerary.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.unibg.magellanus.app.BuildConfig
import com.unibg.magellanus.app.R
import com.unibg.magellanus.app.databinding.FragmentMapBinding
import com.unibg.magellanus.app.itinerary.model.Itinerary
import com.unibg.magellanus.app.itinerary.model.ItineraryRepositoryImpl
import com.unibg.magellanus.app.itinerary.model.network.GeocodingAPI
import com.unibg.magellanus.app.itinerary.model.network.ItineraryAPI
import com.unibg.magellanus.app.itinerary.viewmodel.MapViewModel
import com.unibg.magellanus.app.user.auth.impl.FirebaseAuthenticationProvider
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import org.json.JSONObject
import org.json.JSONTokener
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment(){

    private val provider = FirebaseAuthenticationProvider

    private val viewModel by viewModels<MapViewModel> {
        //TODO capire perchè non funzionano i safeargs
        //var itinerary: Itinerary? = args.itinerary
        var itinerary: Itinerary? = null

        if(itinerary == null)
            itinerary = Itinerary(id = provider.currentUser?.uid, name = "Default itinerary")

        val provider = FirebaseAuthenticationProvider
        val cacheDir = requireContext().cacheDir
        val api = ItineraryAPI.create(provider, cacheDir)
        val geoApi = GeocodingAPI.create(cacheDir)
        val repository = ItineraryRepositoryImpl(api, geoApi)

        MapViewModel.Factory(itinerary!!, repository)
    }

    private lateinit var navController: NavController
    //private val args: MapFragmentArgs by navArgs()

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
        binding.btnPOI.setOnClickListener { btnPOI(this.requireView()) }
        binding.btnItinerari.setOnClickListener { btnItinerari(this.requireView()) }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                if (s.length > 2) {
                    viewModel.search(s)
                    //searchTextChange(s)
                }
                binding.searchBar.clearFocus()
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s.length > 2) {
                    //searchTextChange(s)
                }
                return false
            }
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
           // navController.navigate(MapFragmentDirections.actionMapFragmentToLoginFragment())
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
            myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
            myLocationOverlay.enableMyLocation()
            myLocationOverlay.enableFollowLocation()
            myLocationOverlay.isDrawAccuracyEnabled = true

            this.mapController.setZoom(15)

            val searchOverlay = FolderOverlay()
            val poiOverlay = FolderOverlay()

            map.overlays.apply {
                add(myLocationOverlay)
                add(searchOverlay)
                add(poiOverlay)
            }

            val berlin = Marker(map)
            berlin.position = GeoPoint(52.50693, 13.39748)
            berlin.title = "Berlino"
            map.overlays.add(berlin)

            map.invalidate()

            viewModel.poiSet.observe(viewLifecycleOwner) {
                poiOverlay.items.forEach { marker -> poiOverlay.remove(marker) }
                it.forEach { poi ->
                    val marker = Marker(map)
                    marker.position = GeoPoint(poi.latitude, poi.longitude)
                    marker.title = poi.name
                    //TODO: dovrebbe essere un long click
                    marker.setOnMarkerClickListener { _, _ ->
                        viewModel.removePOI(poi)
                        true
                    }
                    poiOverlay.add(marker)
                }

                map.invalidate()
            }

            viewModel.currentSearch.observe(viewLifecycleOwner) {
                searchOverlay.items.forEach { marker -> searchOverlay.remove(marker) }
                it.forEach { poi ->
                    val marker = Marker(map)
                    marker.position = GeoPoint(poi.latitude, poi.longitude)
                    marker.title = poi.name
                    //TODO: dovrebbe essere un long click
                    marker.setOnMarkerClickListener { _, _ ->
                        viewModel.addPOI(poi)
                        true
                    }
                    searchOverlay.add(marker)
                }

                map.invalidate()
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                requireActivity().lifecycleScope.launch {
                    viewModel.saveChanges()
                }

                navController.popBackStack()
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

    fun btnPOI(view: View) {
        Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_POIFragment)
    }

    fun btnItinerari(view : View){
        Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_itineraryExplorerFragment)

    }

    fun searchTextChange(search: String) {
        val url = "https://photon.komoot.io/api/?q=" + search + "&limit=5"
        var text: JsonElement
        val queue = Volley.newRequestQueue(context)

// Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val jsonObject = JSONTokener(response).nextValue() as JSONObject

                val jsonArray = jsonObject.getJSONArray("features")
                var pos = Marker(map)

                for (i in 0 until jsonArray.length()) {
                    val risultato = jsonArray.getJSONObject(i)
                    var coords = risultato.getJSONObject("geometry").getJSONArray("coordinates")
                    var lat = coords.getDouble(1)
                    var long = coords.getDouble(0)
                    var name = risultato.getJSONObject("properties").getString("name")
                    pos.position = GeoPoint(lat,long)
                    pos.title = name
                    map.overlays.add(pos)
                }
            },
            Response.ErrorListener {
               // text = "That didn't work!"
                //todo iplementare l'errore
            })
// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
     //myLocationOverlay.disableFollowLocation()
    //myLocationOverlay.disableMyLocation()
    //this.mapController.animateTo(GeoPoint(52.50693,13.39748))

}