package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.filterdropdown

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.FilterOptionSelectionListener
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.constants.Filter

/**
 * Created on 01/11/2018.
 */
class FilterOptionView constructor(parent: ViewGroup, val filter: Filter) {

    @BindView(R.id.tv_filteroption)
    lateinit var filterOptionText: TextView

    private var unbinder: Unbinder
    private var filterOptionSelectionListener: FilterOptionSelectionListener? = null
    private val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_filteroption, parent, false)

    init {
        unbinder = ButterKnife.bind(this, view)

        filterOptionText.text = parent.context.getString(filter.resId)

        view.setOnClickListener { filterOptionSelectionListener?.invoke(filter) }

        parent.addView(view)
    }

    fun setFilterOptionSelectionListener(listener: FilterOptionSelectionListener) {
        filterOptionSelectionListener = listener
    }

    fun onDestroyView() {
        view.setOnClickListener(null)

        unbinder.unbind()
    }
}