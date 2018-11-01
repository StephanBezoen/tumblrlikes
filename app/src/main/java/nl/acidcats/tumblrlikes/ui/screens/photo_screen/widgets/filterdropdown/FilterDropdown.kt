package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.filterdropdown

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.FilterOptionSelectionListener
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.constants.Filter

/**
 * Created on 01/11/2018.
 */
class FilterDropdown @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private val filterOptionViews: MutableList<FilterOptionView> = ArrayList()

    init {
        for (filter in Filter.values()) {
            filterOptionViews += (FilterOptionView(this, filter))
        }

        if (!isInEditMode) hide()
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    fun setFilterOptionSelectionListener(listener: FilterOptionSelectionListener) {
        for (optionView in filterOptionViews) {
            optionView.setFilterOptionSelectionListener(listener)
        }
    }

    fun onDestroyView() {
        for (optionView in filterOptionViews) {
            optionView.onDestroyView()
        }
    }
}