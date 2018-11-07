package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.github.ajalt.timberkt.Timber
import kotlinx.android.synthetic.main.navbar.view.*
import nl.acidcats.tumblrlikes.R.layout
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract

/**
 * Created on 31/10/2018.
 */
class PhotoNavBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var filterTypeSelectedListener: FilterTypeSelectedListener? = null
    var navBarListener: PhotoScreenContract.NavBarListener? = null

    init {
        LayoutInflater.from(context).inflate(layout.navbar, this, true)

        filterButton.setOnClickListener { filterDropdown.show() }
        settingsButton.setOnClickListener { navBarListener?.onSettingsRequested() }
        refreshButton.setOnClickListener { navBarListener?.onRefreshRequested() }

        filterDropdown.filterTypeSelectedListener = { setFilter(it, true) }

        hide()
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE

        filterDropdown.hide()
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
