package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoOptionsViewModel;

/**
 * Created by stephan on 28/04/2017.
 */

public class PhotoActionDialog extends FrameLayout {
    public static final String TAG = PhotoActionDialog.class.getSimpleName();

    @BindView(R.id.btn_favorite)
    TextView _favoriteButton;
    @BindView(R.id.btn_hide)
    TextView _hideButton;
    @BindView(R.id.btn_like)
    TextView _likeButton;
    @BindView(R.id.btn_unlike)
    TextView _unlikeButton;
    @BindView(R.id.background)
    View _background;
    @BindView(R.id.txt_view_count)
    TextView _viewCountText;

    private Unbinder _unbinder;
    private PhotoScreenContract.PhotoActionListener _photoActionListener;
    private PhotoOptionsViewModel _viewModel;
    private ViewPropertyAnimator _hideAnimator;
    private int _hideDuration;

    public PhotoActionDialog(Context context) {
        super(context);

        init();
    }

    public PhotoActionDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PhotoActionDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_photo_menu, this, true);
        _unbinder = ButterKnife.bind(this, view);

        _favoriteButton.setOnClickListener(this::onFavoriteButtonClick);
        _likeButton.setOnClickListener(this::onLikeButtonClick);
        _unlikeButton.setOnClickListener(this::onUnlikeButtonClick);
        _hideButton.setOnClickListener(this::onHideButtonClick);

        _background.setOnClickListener(v -> hide(PhotoScreenContract.HideFlow.ANIMATED));

        _hideDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        hide(PhotoScreenContract.HideFlow.INSTANT);
    }

    private void onHideButtonClick(View view) {
        if (_photoActionListener != null) {
            _photoActionListener.onHidePhoto(_viewModel.getPhotoId());
        }

        hide(PhotoScreenContract.HideFlow.INSTANT);
    }

    private void onUnlikeButtonClick(View view) {
        if (_photoActionListener != null) {
            _photoActionListener.onUpdatePhotoLike(_viewModel.getPhotoId(), false);
        }
    }

    private void onLikeButtonClick(View view) {
        if (_photoActionListener != null) {
            _photoActionListener.onUpdatePhotoLike(_viewModel.getPhotoId(), true);
        }
    }

    private void onFavoriteButtonClick(View view) {
        if (_photoActionListener != null) {
            _photoActionListener.onUpdatePhotoFavorite(_viewModel.getPhotoId(), !_viewModel.isPhotoFavorite());
        }
    }

    public void show(@Nonnull PhotoOptionsViewModel viewModel) {
        updateViewModel(viewModel);

        setVisibility(VISIBLE);
        setAlpha(1f);
    }

    public void updateViewModel(@NonNull PhotoOptionsViewModel viewModel) {
        _viewModel = viewModel;

        updateUI();
    }

    private void updateUI() {
        @DrawableRes int iconId = _viewModel.isPhotoFavorite() ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp;
        _favoriteButton.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);

        if (_viewModel.isPhotoLiked()) {
            _likeButton.setText(getContext().getString(R.string.photo_action_like_count, 1));
            _unlikeButton.setText(getContext().getString(R.string.photo_action_unlike));
        } else {
            _likeButton.setText(getContext().getString(R.string.photo_action_like));
            _unlikeButton.setText(getContext().getString(R.string.photo_action_unlike));
        }

        _viewCountText.setText(getContext().getString(R.string.view_count, _viewModel.getViewCount()));
    }

    public void hide(PhotoScreenContract.HideFlow hideFlow) {
        switch (hideFlow) {
            case INSTANT:
                setVisibility(GONE);
                break;
            case ANIMATED:
                if (_hideAnimator == null) {
                    startHideAnimation();
                }
                break;
        }
    }

    private void startHideAnimation() {
        _hideAnimator = animate()
                .alpha(0f)
                .setDuration(_hideDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setVisibility(GONE);

                        _hideAnimator = null;
                    }
                });
    }

    public void setPhotoActionListener(PhotoScreenContract.PhotoActionListener listener) {
        _photoActionListener = listener;
    }

    public void onDestroyView() {
        _favoriteButton.setOnClickListener(null);
        _likeButton.setOnClickListener(null);
        _unlikeButton.setOnClickListener(null);
        _hideButton.setOnClickListener(null);
        _background.setOnClickListener(null);

        _unbinder.unbind();
    }
}
