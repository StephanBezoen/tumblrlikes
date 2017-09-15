package nl.acidcats.tumblrlikes.ui.widgets;


import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.vo.Photo;

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
    private PhotoRepo _photoRepo;
    private Photo _photo;
    private PhotoHiddenListener _photoHiddenListener;

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

    public void setPhotoRepo(PhotoRepo photoRepo) {
        _photoRepo = photoRepo;
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_photo_menu, this, true);
        _unbinder = ButterKnife.bind(this, view);

        _favoriteButton.setOnClickListener(this::onFavoriteButtonClick);
        _likeButton.setOnClickListener(this::onLikeButtonClick);
        _unlikeButton.setOnClickListener(this::onUnlikeButtonClick);
        _hideButton.setOnClickListener(this::onHideButtonClick);

        _background.setOnClickListener(v -> hide());

        hide();
    }

    private void onHideButtonClick(View view) {
        _photoRepo.setPhotoHidden(_photo.id());

        hide();

        if (_photoHiddenListener != null) {
            _photoHiddenListener.onPhotoHidden();
        }
    }

    private void onUnlikeButtonClick(View view) {
        _photoRepo.unlikePhoto(_photo.id());

        hide();
    }

    private void onLikeButtonClick(View view) {
        _photoRepo.likePhoto(_photo.id());

        hide();
    }

    private void onFavoriteButtonClick(View view) {
        _photoRepo.setPhotoFavorite(_photo.id(), !_photo.isFavorite());

        hide();
    }

    public void show(long id) {
        _photo = _photoRepo.getPhotoById(id);
        if (_photo == null) return;

        @DrawableRes int iconId = _photo.isFavorite() ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp;
        _favoriteButton.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);

        int likes = _photo.likeCount();
        if (likes > 0) {
            _likeButton.setText(getContext().getString(R.string.photo_action_like_count, 1));
            _unlikeButton.setText(getContext().getString(R.string.photo_action_unlike));
        } else if (likes < 0) {
            _likeButton.setText(getContext().getString(R.string.photo_action_like));
            _unlikeButton.setText(getContext().getString(R.string.photo_action_unlike_count, -1));
        } else {
            _likeButton.setText(getContext().getString(R.string.photo_action_like));
            _unlikeButton.setText(getContext().getString(R.string.photo_action_unlike));
        }

        _viewCountText.setText(getContext().getString(R.string.view_count, _photo.viewCount()));

        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public interface PhotoHiddenListener {
        void onPhotoHidden();
    }

    public void setPhotoHiddenListener(PhotoHiddenListener listener) {
        _photoHiddenListener = listener;
    }

    public void onDestroy() {
        _favoriteButton.setOnClickListener(null);
        _likeButton.setOnClickListener(null);
        _unlikeButton.setOnClickListener(null);
        _hideButton.setOnClickListener(null);
        _background.setOnClickListener(null);

        _unbinder.unbind();
    }
}
