package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.filterdropdown

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import nl.acidcats.tumblrlikes.R

/**
 * Created on 01/11/2018.
 */
class DropdownMenuOptionView constructor(parent: ViewGroup, label: String, private val handler: () -> Unit) {

    private val view = LayoutInflater.from(parent.context).inflate(R.layout.dropdown_menuoption, parent, false)!!

    init {
        view.findViewById<TextView>(R.id.menuOptionText).text = label

        view.setOnClickListener { handler.invoke() }

        parent.addView(view)
    }

    fun onDestroyView() {
        view.setOnClickListener(null)
    }
}
