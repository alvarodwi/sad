package me.varoa.sad.ui.screen.maps

import android.Manifest
import android.content.Intent
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import me.varoa.sad.R
import me.varoa.sad.core.data.parcelize
import me.varoa.sad.databinding.FragmentMapsBinding
import me.varoa.sad.ui.base.BaseEvent.ShowErrorMessage
import me.varoa.sad.ui.base.BaseFragment
import me.varoa.sad.ui.ext.checkPermission
import me.varoa.sad.ui.ext.snackbar
import me.varoa.sad.ui.ext.toast
import me.varoa.sad.ui.ext.viewBinding
import me.varoa.sad.ui.parcelable.ParcelableStory

class MapsFragment : BaseFragment(R.layout.fragment_maps) {
    private val binding by viewBinding<FragmentMapsBinding>()
    private val viewModel by viewModels<MapsViewModel>()

    private lateinit var _map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    is MapEvent.EndOfPagination -> {
                        snackbar(getString(R.string.info_end_of_pagination))
                        binding.btnLoadMore.isVisible = false
                    }
                    is ShowErrorMessage -> {
                        logcat { "Error : ${event.message}" }
                        snackbar("Error : ${event.message}")
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private val callback = OnMapReadyCallback { googleMap ->
        _map = googleMap

        try {
            _map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.custom_map_style
                )
            )
        } catch (e: Resources.NotFoundException) {
            logcat { "Can't find style. Error: $e" }
        }

        _map.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isRotateGesturesEnabled = true
        }

        getCurrentLocation()
        loadStoryMarkers()
    }

    override fun bindView() {
        val mapView = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment
        mapView.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.btnLoadMore.setOnClickListener { viewModel.onLoadMore() }
    }

    private fun loadStoryMarkers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stories.collect { stories ->
                stories.forEach { story ->
                    if (story.lat == null || story.lon == null) return@forEach
                    val latLng = LatLng(story.lat, story.lon)

                    _map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(story.name)
                            .snippet(story.id)
                    ).also { it?.tag = story.parcelize() }

                    _map.setOnInfoWindowClickListener {
                        findNavController().navigate(
                            MapsFragmentDirections.actionToDetailStory(it.tag as ParcelableStory)
                        )
                    }
                }
            }
        }
    }

    // location shenanigans
    private fun getCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    _map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            10f // city-wide zoom
                        )
                    )
                } else {
                    toast(getString(R.string.err_location_not_found))
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_location_permission_needed_title)
            message(R.string.dialog_location_permission_needed_message)
            positiveButton(android.R.string.ok) {
                requestLocationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            negativeButton(android.R.string.cancel) {
                findNavController().popBackStack()
            }
        }
    }

    private fun onPermissionDenied() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_location_permission_rejected_title)
            message(R.string.dialog_location_permission_rejected_message)
            positiveButton(android.R.string.ok) {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                    it.data = Uri.fromParts("package", requireActivity().packageName, null)
                    startActivity(it)
                }
            }
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getCurrentLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    getCurrentLocation()
                }
                else -> {
                    onPermissionDenied()
                }
            }
        }
}
