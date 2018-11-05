package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.R.layout
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.filterdropdown.FilterDropdown

/**
 * Created on 31/10/2018.
 */
class PhotoNavBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.btn_filter)
    lateinit var filterButton: TextView
    @BindView(R.id.btn_settings)
    lateinit var settingsButton: View
    @BindView(R.id.btn_refresh)
    lateinit var refreshButton: View
    @BindView(R.id.filter_dropdown)
    lateinit var filterDropdown: FilterDropdown

    var filterTypeSelectedListener: FilterTypeSelectedListener? = null

    private var unbinder: Unbinder

    init {
        val view = LayoutInflater.from(context).inflate(layout.navbar, this, true)
        unbinder = ButterKnife.bind(this, view)

        filterButton.setOnClickListener { filterDropdown.show() }
        settingsButton.setOnClickListener { sendBroadcast(Broadcasts.SETTINGS_REQUEST) }
        refreshButton.setOnClickListener { sendBroadcast(Broadcasts.REFRESH_REQUEST) }

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

    private fun sendBroadcast(action: String) = LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(action))

    fun onDestroyView() {
        filterButton.setOnClickListener(null)
        settingsButton.setOnClickListener(null)
        refreshButton.setOnClickListener(null)

        unbinder.unbind()
    }
}