package me.varoa.sad.ui.screen.story.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.location.Location
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.request.ImageRequest
import coil.size.Scale
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import me.varoa.sad.R
import me.varoa.sad.core.domain.model.NewStory
import me.varoa.sad.databinding.FragmentAddStoryBinding
import me.varoa.sad.ui.base.BaseEvent.ShowErrorMessage
import me.varoa.sad.ui.base.BaseFragment
import me.varoa.sad.ui.ext.checkPermission
import me.varoa.sad.ui.ext.createCustomTempFile
import me.varoa.sad.ui.ext.snackbar
import me.varoa.sad.ui.ext.toast
import me.varoa.sad.ui.ext.uriToFile
import me.varoa.sad.ui.ext.viewBinding
import me.varoa.sad.ui.screen.story.StoryEvent
import java.io.File

class AddStoryFragment : BaseFragment(R.layout.fragment_add_story) {
  private val binding by viewBinding<FragmentAddStoryBinding>()
  private val viewModel by viewModels<AddStoryViewModel>()

  private lateinit var _photo: File
  private var _location: LatLng? = null
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  override fun onStart() {
    super.onStart()
    eventJob = viewModel.events
      .onEach { event ->
        when (event) {
          is StoryEvent.StoryAdded -> {
            snackbar(getString(R.string.info_story_added))
            findNavController().navigate(
              AddStoryFragmentDirections.actionToListStory()
            )
          }
          is ShowErrorMessage -> {
            logcat { "Error : ${event.message}" }
            snackbar("Error : ${event.message}")
          }
        }
      }.launchIn(viewLifecycleOwner.lifecycleScope)
  }

  override fun bindView() {
    binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    binding.btnCamera.setOnClickListener { startCamera() }
    binding.btnGallery.setOnClickListener { startGallery() }
    binding.buttonAdd.setOnClickListener { onAddStoryClicked() }
    binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        getCurrentLocation()
      } else {
        _location = null
      }
    }

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
  }

  private fun onAddStoryClicked() {
    if (binding.edAddDescription.text.toString().isEmpty()) {
      snackbar(getString(R.string.err_empty_description))
      return
    }
    if (!::_photo.isInitialized) {
      snackbar(getString(R.string.err_empty_picture))
      return
    }

    var data = NewStory(
      description = binding.edAddDescription.text.toString(),
      photo = _photo,
    )
    // optionally add location data if not null
    _location?.let {
      data = data.copy(
        lat = it.latitude,
        lon = it.longitude
      )
    }
    viewModel.onAddStory(data)
  }

  private lateinit var currentPhotoPath: String
  private val launcherIntentCamera = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) {
    if (it.resultCode == RESULT_OK) {
      viewLifecycleOwner.lifecycleScope.launch {
        _photo = Compressor.compress(requireContext(), File(currentPhotoPath))

        binding.ivPreviewPhoto.apply {
          val request = ImageRequest.Builder(requireContext())
            .data(_photo)
            .scale(Scale.FILL)
            .target(this)
            .build()
          imageLoader.enqueue(request)
        }
      }
    }
  }

  private val launcherIntentGallery = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == RESULT_OK) {
      viewLifecycleOwner.lifecycleScope.launch {
        val selectedImg: Uri = result.data?.data as Uri
        _photo = Compressor.compress(requireContext(), uriToFile(selectedImg, requireContext()))

        binding.ivPreviewPhoto.apply {
          val request = ImageRequest.Builder(requireContext())
            .data(selectedImg)
            .scale(Scale.FILL)
            .target(this)
            .build()
          imageLoader.enqueue(request)
        }
      }
    }
  }

  private fun startCamera() {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.resolveActivity(requireActivity().packageManager)

    createCustomTempFile(requireContext()).also {
      val photoURI: Uri = FileProvider.getUriForFile(
        requireContext(),
        requireContext().packageName,
        it
      )
      currentPhotoPath = it.absolutePath
      intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
      launcherIntentCamera.launch(intent)
    }
  }

  private fun startGallery() {
    val intent = Intent()
    intent.action = ACTION_GET_CONTENT
    intent.type = "image/*"
    val chooser = Intent.createChooser(intent, getString(R.string.lbl_choose_picture))
    launcherIntentGallery.launch(chooser)
  }

  // location shenanigans
  private fun getCurrentLocation() {
    if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
      checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    ) {
      fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
          _location = LatLng(location.latitude, location.longitude)
          logcat { "_location -> $_location" }
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
        binding.cbLocation.isChecked = false
      }
    }
  }

  private fun onPermissionDenied() {
    binding.cbLocation.isChecked = false
    MaterialDialog(requireContext()).show {
      title(R.string.dialog_location_permission_rejected_title)
      message(R.string.dialog_location_permission_rejected_message)
      positiveButton(android.R.string.ok) {
        binding.cbLocation.isChecked = false
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
