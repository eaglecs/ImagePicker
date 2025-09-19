package com.github.dhaval2404.imagepicker.sample

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat


fun Activity.setEdgeToEdge(targetView: View) {
    // Cho phép layout vẽ edge-to-edge
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // Lắng nghe insets (status bar, nav bar, ime…)
    ViewCompat.setOnApplyWindowInsetsListener(targetView) { view, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        view.setPadding(
            view.paddingLeft,
            view.paddingTop + statusBarHeight, // đẩy xuống dưới status bar
            view.paddingRight,
            view.paddingBottom
        )
        insets
    }
}