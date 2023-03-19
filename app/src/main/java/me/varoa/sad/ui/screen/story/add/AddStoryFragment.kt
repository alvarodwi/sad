package me.varoa.sad.ui.screen.story.add

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.request.ImageRequest
import coil.size.Scale
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
import me.varoa.sad.ui.ext.createCustomTempFile
import me.varoa.sad.ui.ext.snackbar
import me.varoa.sad.ui.ext.uriToFile
import me.varoa.sad.ui.ext.viewBinding
import me.varoa.sad.ui.screen.story.StoryEvent
import java.io.File

class AddStoryFragment : BaseFragment(R.layout.fragment_add_story) {
  private val binding by viewBinding<FragmentAddStoryBinding>()
  private val viewModel by viewModels<AddStoryViewModel>()

  private lateinit var _photo: File

  override fun onStart() {
    super.onStart()
    eventJob = viewModel.events
      .onEach { event ->
        when (event) {
          is StoryEvent.StoryAdded -> {
            snackbar(getString(R.string.info_story_added))
            findNavController().popBackStack()
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

    val data = NewStory(
      description = binding.edAddDescription.text.toString(),
      photo = _photo
    )
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
}