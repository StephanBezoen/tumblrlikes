package nl.acidcats.tumblrlikes.ui.screens.base;

/**
 * Created on 18/10/2018.
 */
public interface BasePresenter<T> {
    void setView(T view);

    void onDestroyView();
}
