package me.varoa.sad.ui.ext

import android.app.Application
import android.graphics.Bitmap
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import me.varoa.sad.R
import java.io.File
import java.util.Locale

private fun Fragment.createSnackbar(
  message: String,
  duration: Int
): Snackbar = Snackbar.make(requireView(), message, duration)

fun Fragment.snackbar(
  message: String,
  duration: Int = Snackbar.LENGTH_SHORT
) {
  createSnackbar(message, duration).show()
}

fun Fragment.snackbar(
  message: String,
  anchorView: View,
  duration: Int = Snackbar.LENGTH_SHORT
) {
  createSnackbar(message, duration).apply { setAnchorView(anchorView) }.show()
}

fun toggleAppTheme(value: AppTheme) {
  AppCompatDelegate.setDefaultNightMode(
    when (value) {
      AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
      AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
      AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
  )
}

