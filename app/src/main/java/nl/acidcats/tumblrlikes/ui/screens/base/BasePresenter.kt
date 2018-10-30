package nl.acidcats.tumblrlikes.ui.screens.base

/**
 * Created on 18/10/2018.
 */
interface BasePresenter<T> {
    fun setView(view: T)

    fun notify(action: String)

    fun onDestroyView()
}
