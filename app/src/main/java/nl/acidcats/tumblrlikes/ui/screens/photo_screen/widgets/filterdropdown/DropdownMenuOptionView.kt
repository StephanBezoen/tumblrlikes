package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.filterdropdown

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import nl.acidcats.tumblrlikes.R

/**
 * Created on 01/11/2018.
 */
class DropdownMenuOptionView constructor(parent: ViewGroup, label: String, private val handler: () -> Unit) {

    @BindView(R.id.tv_menuoption)
    lateinit var menuOptionText: TextView

    private var unbinder: Unbinder
    private val view = LayoutInflater.from(parent.context).inflate(R.layout.dropdown_menuoption, parent, false)

    init {
        unbinder = ButterKnife.bind(this, view)

        menuOptionText.text = label

        view.setOnClickListener { handler.invoke() }

        parent.addView(view)
    }

    fun onDestroyView() {
        view.setOnClickListener(null)

        unbinder.unbind()
    }
}
