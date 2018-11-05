package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.filterdropdown

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.FilterTypeSelectedListener

/**
 * Created on 01/11/2018.
 */
class FilterDropdown @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    var filterTypeSelectedListener: FilterTypeSelectedListener? = null

    private val filterOptionViews: MutableList<DropdownMenuOptionView> = ArrayList()

    init {
        for (filter in Filter.values()) {
            filterOptionViews += (DropdownMenuOptionView(this, context.getString(filter.resId)) {
                filterTypeSelectedListener?.invoke(filter.filterType)
            })
        }

        if (!isInEditMode) hide()
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    fun getFilterLabel(filterType: FilterType): String = context.getString(Filter.getFilterByType(filterType).resId)

    fun onDestroyView() {
        for (optionView in filterOptionViews) {
            optionView.onDestroyView()
        }
    }
}
