package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets

import android.graphics.PointF
import nl.acidcats.tumblrlikes.core.constants.FilterType

/**
 * Created on 12/11/2018.
 */
typealias FilterTypeSelectedListener = (FilterType) -> Unit

typealias ImageGestureListener = (InteractiveImageView.Gesture) -> Unit