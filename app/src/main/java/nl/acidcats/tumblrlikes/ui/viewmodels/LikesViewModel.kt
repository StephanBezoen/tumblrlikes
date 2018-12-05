package nl.acidcats.tumblrlikes.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesUseCase
import nl.acidcats.tumblrlikes.ui.screens.base.BaseViewModel
import java.util.*
import javax.inject.Inject

/**
 * Created on 05/12/2018.
 */
class LikesViewModel @Inject constructor(private var getLikesUseCase: GetLikesUseCase) : BaseViewModel() {
    enum class RefreshType {
        AUTOMATIC, MANUAL
    }

    sealed class LoadingState {
        object IDLE : LoadingState()
        object LOADING : LoadingState()
        object SUCCESS : LoadingState()
        object ERROR : LoadingState()
    }

    private var loadingState = MutableLiveData<LoadingState>()
    private var loadedLikesCount = MutableLiveData<Int>()
    private val loadingInterruptor: MutableList<Boolean> = ArrayList()

    init {
        loadingState.value = LoadingState.IDLE
    }

    fun getLoadingState(): LiveData<LoadingState> = loadingState

    fun getLoadedLikesCount(): LiveData<Int> = loadedLikesCount

    fun refreshLikes(refreshType: RefreshType) {
        loadingState.value = LoadingState.LOADING

        registerSubscription(
                getLikesUseCase
                        .loadAllLikes(LoadLikesMode.SINCE_LAST, loadingInterruptor, Date().time)
                        .subscribe({ handleLikesLoaded(it, refreshType) }, { handleLoadLikesError() })
        )
    }

    private fun handleLoadLikesError() {
        loadingState.value = LoadingState.ERROR
    }

    private fun handleLikesLoaded(count: Int, refreshType: RefreshType) {
        loadedLikesCount.value = count

        loadingState.value = when (refreshType) {
            RefreshType.MANUAL -> LoadingState.SUCCESS
            RefreshType.AUTOMATIC -> if (count > 0) LoadingState.SUCCESS else LoadingState.IDLE
        }
    }

    fun resetLoadingState() {
        loadedLikesCount.value = 0
        loadingState.value = LoadingState.IDLE
    }
}
