package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.github.ajalt.timberkt.Timber
import kotlinx.android.synthetic.main.navbar.view.*
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract

/**
 * Created on 31/10/2018.
 */
class PhotoNavBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var filterTypeSelectedListener: FilterTypeSelectedListener? = null
    var navBarListener: PhotoScreenContract.NavBarListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.navbar, this, true)

        filterButton.setOnClickListener { filterDropdown.show() }
        settingsButton.setOnClickListener { navBarListener?.onSettingsRequested() }
        refreshButton.setOnClickListener { navBarListener?.onRefreshRequested() }

        filterDropdown.filterTypeSelectedListener = { setFilter(it, true) }

        visibility = View.GONE
    }

    fun show() {
        visibility = View.VISIBLE

        if (height == 0) {

        }

        translationY = -height.toFloat()
        animate()
                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .translationY(0f)
                .setListener(null)
    }

    fun hide() {
        Timber.d { "hide: " }

        filterDropdown.hide()

        animate()
                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .translationY(-height.toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        visibility = View.GONE
                    }
                })
    }

    fun setFilter(filterType: FilterType) = setFilter(filterType, false)

    private fun setFilter(filterType: FilterType, notifyListener: Boolean) {
        filterButton.text = filterDropdown.getFilterLabel(filterType)

        filterDropdown.hide()

        if (notifyListener) {
            filterTypeSelectedListener?.invoke(filterType)
        }
    }

    fun enableRefreshButton(enabled: Boolean) {
        Timber.d { "enableRefreshButton: $enabled" }

        refreshButton.isEnabled = enabled
        refreshButton.alpha = if (enabled) 1f else .2f
    }

    fun onDestroyView() {
        filterButton.setOnClickListener(null)
        settingsButton.setOnClickListener(null)
        refreshButton.setOnClickListener(null)
    }
}
