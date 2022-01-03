package com.unibg.magellanus.app.itinerary.view

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.unibg.magellanus.app.BuildConfig
import com.unibg.magellanus.app.databinding.FragmentMapBinding
import com.unibg.magellanus.app.itinerary.viewmodel.MapViewModel
import com.unibg.magellanus.app.user.auth.impl.FirebaseAuthenticationProvider
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.Toast
import com.unibg.magellanus.app.R
import org.osmdroid.api.IMapController
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider

import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2


class MapFragment : Fragment() {

    private val provider = FirebaseAuthenticationProvider()

    private val viewModel by viewModels<MapViewModel>()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentMapBinding
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    private lateinit var map: MapView
    private lateinit var mapController: IMapController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.bntCentra.setOnClickListener{test()}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        val ctx = requireActivity().applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.visibility = View.VISIBLE



        if (!provider.isUserLoggedIn())
        navController.navigate(MapFragmentDirections.actionMapFragmentToLoginFragment())
        else {

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

        //Controller usage, overlay declaration and location enabling
            this.mapController = map.controller
            myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
            myLocationOverlay.enableMyLocation()
            myLocationOverlay.enableFollowLocation()
            myLocationOverlay.isDrawAccuracyEnabled = true

            this.mapController.setZoom(15)
            var compassOverlay =
                CompassOverlay(context, InternalCompassOrientationProvider(context), map)
            compassOverlay.enableCompass()
            map.overlays.add(compassOverlay)
            map.overlays.add(myLocationOverlay)
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

    fun test(){
        this.mapController.animateTo(myLocationOverlay.myLocation)
        this.mapController.setZoom(15)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.isDrawAccuracyEnabled = true
    }
}