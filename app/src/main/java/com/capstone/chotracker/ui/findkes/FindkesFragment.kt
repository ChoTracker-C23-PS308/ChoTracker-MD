package com.capstone.chotracker.ui.findkes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.chotracker.R
import com.capstone.chotracker.custom_view.CustomPopUpAlert
import com.capstone.chotracker.data.response.findkes.FindkesModel
import com.capstone.chotracker.databinding.FragmentFindkesBinding
import com.capstone.chotracker.databinding.FragmentHomeBinding
import com.capstone.chotracker.utils.ResultCondition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior


class FindkesFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentFindkesBinding? = null
    private val binding get() = _binding!!

    private var currentLat: Double? = null
    private var currentLon: Double? = null
    private var currentLocation: String? = null
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: FindkesViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var hospitalAdapter: FindkesAdapter

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindkesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(FindkesViewModel::class.java)
        setupRecyclerView(requireContext())
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setMaxHeight(1000)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mMap.uiSettings?.setScrollGesturesEnabled(true)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        mMap.uiSettings?.setScrollGesturesEnabled(false)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermission()
            } else {
                getCurrentLocation()
            }
        } else {
            getCurrentLocation()
        }
        observeFindkesMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    mMap.isMyLocationEnabled = true
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    currentLat = location.latitude
                    currentLon = location.longitude
                    currentLocation = "$currentLat,$currentLon"
                    viewModel.getNearbyFindkes(currentLocation ?: "")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    private fun observeFindkesMap() {
        viewModel.resultFindkes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ResultCondition.LoadingState -> {
                    loadingHandler(true)
                }
                is ResultCondition.SuccessState -> {
                    loadingHandler(false)
                    mMap.clear()
                    val findkesList = result.data
                    hospitalAdapter.submitList(findkesList.toMutableList())
                    val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.hospital_marker_image)
                    val bitmap = drawable?.toBitmap()
                    for (findkes in findkesList) {
                        val markerOptions = MarkerOptions()
                            .position(
                                LatLng(
                                    findkes.geometry.location.latitude,
                                    findkes.geometry.location.longitude
                                )
                            )
                            .title(findkes.name)
                            .snippet(findkes.vicinity)
                            .icon(bitmap?.let { BitmapDescriptorFactory.fromBitmap(it) })
                        mMap.addMarker(markerOptions)
                    }
                }
                is ResultCondition.ErrorState -> {
                    loadingHandler(false)
                    errorHandler(true)
                }
            }
        })
    }

    private fun setupRecyclerView(context: Context) {
        val rvHospital = binding.layoutBottomSheet.rvHospital
        val layoutManager = if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(context, 2)
        } else {
            LinearLayoutManager(context)
        }
        rvHospital.layoutManager = layoutManager
        hospitalAdapter = FindkesAdapter(context)
        rvHospital.adapter = hospitalAdapter
        hospitalAdapter.setOnItemClickListener(itemClickListener)
    }

    private val itemClickListener = object : FindkesAdapter.OnItemClickListener {
        override fun onItemClick(hospital: FindkesModel) {
            val markerPosition = LatLng(
                hospital.geometry.location.latitude,
                hospital.geometry.location.longitude
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15f))
        }
    }

    private fun errorHandler(error: Boolean) {
        if (error) {
            val alert = CustomPopUpAlert(requireContext(), R.string.error_message)
            alert.show()
            alert.setOnDismissListener {
                requireActivity().finish()
            }
        }
    }

    private fun loadingHandler(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarMap.visibility = View.VISIBLE
            binding.root.visibility = View.INVISIBLE
        } else {
            binding.progressBarMap.visibility = View.INVISIBLE
            binding.root.visibility = View.VISIBLE
        }
    }
}