package nl.acidcats.tumblrlikes.ui.screens.photo_screen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import nl.acidcats.tumblrlikes.core.constants.FilterType;
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter;
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoActionDialogViewModel;

/**
 * Created on 19/10/2018.
 */
public interface PhotoScreenContract {
    enum HideFlow {
        INSTANT, ANIMATED
    }

    interface Presenter extends BasePresenter<View>, PhotoActionListener, GestureListener {
        void onViewCreated();

        void onImageLoadFailed();

        void onFilterTypeSelected(FilterType filterType);

        void saveState(@NonNull Bundle outState);

        void restoreState(@Nullable Bundle savedInstanceState, @Nullable Bundle args);

        void onPause();

        void onResume();
    }

    interface View extends BaseView {
        void loadPhoto(String url, boolean notifyOnError);

        void resetPhotoScale();

        void hidePhotoActionDialog(PhotoScreenContract.HideFlow hideFlow);

        void showPhotoActionDialog(PhotoActionDialogViewModel viewModel);

        void setFilterType(FilterType filterType);

        void setActionDialogViewModel(PhotoActionDialogViewModel viewModel);

        void showUI();

        void hideUI();

        void setPhotoVisible(boolean visible);
    }

    interface PhotoActionListener {
        void onHidePhoto(long id);

        void onUpdatePhotoLike(long id, boolean isLiked);

        void onUpdatePhotoFavorite(long id, boolean isFavorite);
    }

    interface GestureListener {
        void onSwipe();

        void onTap();

        void onLongPress();

        void onDoubleTap();
    }
}
