package nl.acidcats.tumblrlikes.ui.screens.photo_screen;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.usecases.photos.GetFilteredPhotoUseCase;
import nl.acidcats.tumblrlikes.core.usecases.photos.PhotoFilterUseCase;
import nl.acidcats.tumblrlikes.core.usecases.photos.PhotoViewUseCase;
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoPropertyUseCase;
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.constants.Filter;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoOptionsViewModel;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoViewViewModel;
import rx.Observable;

/**
 * Created on 19/10/2018.
 */
public class PhotoScreenPresenter extends BasePresenterImpl<PhotoScreenContract.View> implements PhotoScreenContract.Presenter {
    private static final String TAG = PhotoScreenPresenter.class.getSimpleName();

    private static final String KEY_VIEW_MODEL = "key_" + TAG + "_viewModel";

    @Inject
    UpdatePhotoPropertyUseCase _updatePhotoPropertyUseCase;
    @Inject
    PhotoViewUseCase _photoViewUseCase;
    @Inject
    PhotoFilterUseCase _photoFilterUseCase;
    @Inject
    GetFilteredPhotoUseCase _getFilteredPhotoUseCase;

    @Nullable
    private PhotoViewViewModel _viewModel;

    @Inject
    PhotoScreenPresenter() {
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        if (PhotoViewViewModel.Companion.isValid(_viewModel)) {
            outState.putParcelable(KEY_VIEW_MODEL, _viewModel);
        }
    }

    @Override
    public void restoreState(@Nullable Bundle savedInstanceState, @Nullable Bundle args) {
        if (savedInstanceState != null) {
            _viewModel = savedInstanceState.getParcelable(KEY_VIEW_MODEL);
        }
    }

    @Override
    public void onViewCreated() {
        registerSubscription(
                _photoFilterUseCase
                        .getSelectedFilterType()
                        .subscribe(filterType -> {
                            if (getView() != null) {
                                getView().setFilter(Filter.getFilterByType(filterType));
                            }
                        })
        );

        showNextPhoto();
    }

    @Override
    public void onImageLoadFailed() {
        if (getView() != null && PhotoViewViewModel.Companion.isValid(_viewModel)) {
            getView().loadPhoto(_viewModel.getFallbackUrl(), false);
        }
    }

    @Override
    public void onFilterSelected(Filter filter) {
        registerSubscription(
                _photoFilterUseCase
                        .storeFilterSelection(filter.getFilterType())
                        .subscribe(isStored -> showNextPhoto())
        );
    }

    @Override
    public void onPause() {
        endPhotoView();

        if (getView() != null) {
            getView().setPhotoVisible(false);
        }
    }

    @Override
    public void onResume() {
        if (getView() != null) {
            getView().setPhotoVisible(true);
        }

        startPhotoView();

        getView().hideUI();
    }

    @Override
    public void onHidePhoto(long id) {
        registerSubscription(
                _updatePhotoPropertyUseCase
                        .setHidden(id)
                        .subscribe(photo -> {
                            if (getView() != null) {
                                getView().hidePhotoActionDialog(PhotoScreenContract.HideFlow.INSTANT);
                            }

                            showNextPhoto();
                        })
        );
    }

    @Override
    public void onUpdatePhotoLike(long id, boolean isLiked) {
        registerSubscription(
                _updatePhotoPropertyUseCase
                        .updateLike(id, isLiked)
                        .subscribe(this::onPhotoPropertyUpdated)
        );
    }

    @Override
    public void onUpdatePhotoFavorite(long id, boolean isFavorite) {
        registerSubscription(
                _updatePhotoPropertyUseCase
                        .updateFavorite(id, isFavorite)
                        .subscribe(this::onPhotoPropertyUpdated)
        );
    }

    private void onPhotoPropertyUpdated(@Nullable Photo photo) {
        createViewModel(photo);

        if (getView() != null && PhotoViewViewModel.Companion.isValid(_viewModel)) {
            getView().setPhotoOptionsViewModel(createPhotoOptionsViewModel(_viewModel));
        }

        if (getView() != null) {
            getView().hidePhotoActionDialog(PhotoScreenContract.HideFlow.ANIMATED);
        }

    }

    private void showNextPhoto() {
        if (getView() != null) {
            getView().resetPhotoScale();
        }

        if (PhotoViewViewModel.Companion.isValid(_viewModel)) {
            endPhotoView();
        }

        registerSubscription(
                _getFilteredPhotoUseCase
                        .getNextFilteredPhoto()
                        .map(this::createViewModel)
                        .filter(PhotoViewViewModel.Companion::isValid)
                        .flatMap(viewModel -> {
                            if (getView() != null) {
                                getView().loadPhoto(viewModel.getUrl(), true);
                            }

                            return Observable.just(viewModel);
                        })
                        .flatMap(viewModel -> _photoViewUseCase.startPhotoView(viewModel.getPhotoId(), SystemClock.elapsedRealtime()))
                        .subscribe(
                                isViewStarted -> Log.d(TAG, "showPhoto: "),
                                throwable -> Log.e(TAG, "showPhoto: " + throwable.getMessage()))
        );
    }

    private void startPhotoView() {
        if (PhotoViewViewModel.Companion.isValid(_viewModel)) {
            registerSubscription(_photoViewUseCase.startPhotoView(_viewModel.getPhotoId(), SystemClock.elapsedRealtime()).subscribe());
        }
    }

    private void endPhotoView() {
        if (PhotoViewViewModel.Companion.isValid(_viewModel)) {
            registerSubscription(_photoViewUseCase.endPhotoView(_viewModel.getPhotoId(), SystemClock.elapsedRealtime()).subscribe());
        }
    }

    @Nullable
    private PhotoViewViewModel createViewModel(@Nullable Photo photo) {
        if (photo == null) return null;
        String url = photo.isCached() ? photo.getFilePath() : photo.getUrl();
        if (url != null && !url.startsWith("http")) url = "file:" + url;

        _viewModel = new PhotoViewViewModel(
                photo.getId(),
                url,
                photo.getUrl(),
                photo.isFavorite(),
                photo.isLiked(),
                photo.getViewCount());

        return _viewModel;
    }

    @NonNull
    private PhotoOptionsViewModel createPhotoOptionsViewModel(@NonNull PhotoViewViewModel photoFragmentViewModel) {
        return new PhotoOptionsViewModel(
                photoFragmentViewModel.getPhotoId(),
                photoFragmentViewModel.isFavorite(),
                photoFragmentViewModel.isLiked(),
                photoFragmentViewModel.getViewCount());
    }

    @Override
    public void onSwipe() {
        showNextPhoto();
    }

    @Override
    public void onTap() {
        if (getView() != null) {
            getView().showUI();
        }
    }

    @Override
    public void onLongPress() {
        if (getView() != null && PhotoViewViewModel.Companion.isValid(_viewModel)) {
            getView().showPhotoActionDialog(createPhotoOptionsViewModel(_viewModel));
        }
    }

    @Override
    public void onDoubleTap() {
        if (getView() != null) {
            getView().resetPhotoScale();
        }
    }
}
