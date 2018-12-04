package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.photooptions

import androidx.lifecycle.LifecycleOwner
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenViewModel

/**
 * Created on 03/12/2018.
 */
typealias OptionSelectedListener = (option: PhotoOptionsContract.Option) -> Unit

interface PhotoOptionsContract {

    enum class Option {
        FAVORITE, LIKE, HIDE, SAVE
    }

    enum class HideFlow {
        INSTANT, ANIMATED
    }

    interface View {
        fun show()

        fun hide(hideFlow: HideFlow)

        fun setOptionSelectedListener(listener: OptionSelectedListener)

        fun initViewModel(viewModel: PhotoScreenViewModel, lifecycleOwner: LifecycleOwner)

        fun onDestroyView()
    }
}