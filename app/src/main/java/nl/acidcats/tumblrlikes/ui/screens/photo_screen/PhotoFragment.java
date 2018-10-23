package nl.acidcats.tumblrlikes.ui.screens.photo_screen;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

import butterknife.BindView;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.core.constants.FilterType;
import nl.acidcats.tumblrlikes.core.usecases.photos.PhotoViewUseCase;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoActionDialogViewModel;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.InteractiveImageView;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.PhotoActionDialog;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.PhotoNavBar;
import nl.acidcats.tumblrlikes.util.GlideApp;

/**
 * Created by stephan on 13/04/2017.
 */

public class PhotoFragment extends BaseFragment implements PhotoScreenContract.View {
    private static final String TAG = PhotoFragment.class.getSimpleName();

    private static final long HIDE_UI_DELAY_MS = 2000L;

    @Inject
    PhotoViewUseCase _photoViewUseCase;
    @Inject
    PhotoScreenPresenter _presenter;

    @BindView(R.id.photo)
    InteractiveImageView _photoView;
    @BindView(R.id.photo_action_dialog)
    PhotoActionDialog _photoActionDialog;
    @BindView(R.id.photo_nav_bar)
    PhotoNavBar _photoNavBar;

    private Handler _handler = new Handler();
    private Runnable _uiHider = this::hideUI;
    private boolean _isTest = true;

    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Override
    protected void injectFrom(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _presenter.restoreState(savedInstanceState, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _presenter.setView(this);
        _presenter.onViewCreated();

        _photoView.setGestureListener(this::onGesture);

        _photoActionDialog.setPhotoActionListener(_presenter);

        _photoNavBar.setFilterOptionSelectionListener(filterType -> _presenter.onFilterTypeSelected(filterType));

        if (_isTest) {
            _photoView.setAlpha(0.1f);
        }
    }

    private void onGesture(InteractiveImageView.Gesture gesture) {
        switch (gesture) {
            case SIDE_SWIPE:
                _presenter.onSwipe();
                break;
            case TAP:
                _presenter.onTap();
                break;
            case LONG_PRESS:
                _presenter.onLongPress();
                break;
            case DOUBLE_TAP:
                _presenter.onDoubleTap();
                break;
        }
    }

    @Override
    public void showPhotoActionDialog(PhotoActionDialogViewModel viewModel) {
        _photoActionDialog.show(viewModel);
    }

    @Override
    public void setActionDialogViewModel(PhotoActionDialogViewModel viewModel) {
        _photoActionDialog.updateViewModel(viewModel);
    }

    @Override
    public void hidePhotoActionDialog(PhotoScreenContract.HideFlow hideFlow) {
        _photoActionDialog.hide(hideFlow);
    }

    @Override
    public void setFilterType(FilterType filterType) {
        _photoNavBar.setFilterType(filterType);
    }

    @Override
    public void showUI() {
        _photoView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        _handler.removeCallbacks(_uiHider);
        _handler.postDelayed(_uiHider, HIDE_UI_DELAY_MS);

        _photoNavBar.show();
    }

    @Override
    public void hideUI() {
        _photoView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );

        _photoNavBar.hide();
    }

    @Override
    public void resetPhotoScale() {
        _photoView.resetScale();
    }

    @Override
    public void loadPhoto(String url, boolean notifyOnError) {
        @Nullable RequestListener<Drawable> listener = null;
        if (notifyOnError) {
            listener = new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    _presenter.onImageLoadFailed();

                    return true;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            };
        }

        loadPhoto(url, listener);
    }

    private void loadPhoto(String url, @Nullable RequestListener<Drawable> listener) {
        if (getContext() == null) return;

        GlideApp.with(getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(listener)
                .into(new DrawableImageViewTarget(_photoView));
    }

    @Override
    public void setPhotoVisible(boolean visible) {
        _photoView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        _presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        _presenter.onPause();

        _handler.removeCallbacks(_uiHider);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        _presenter.saveState(outState);
    }

    @Override
    public void onDestroyView() {
        _photoActionDialog.onDestroyView();
        _photoView.onDestroyView();

        _presenter.onDestroyView();

        super.onDestroyView();
    }
}
