package nl.acidcats.tumblrlikes.ui.widgets;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;

/**
 * Created by stephan on 28/04/2017.
 */

public class PhotoActionDialog extends DialogFragment {
    public static final String TAG = PhotoActionDialog.class.getSimpleName();

    private static final String KEY_PHOTO_URL = "key_photoUrl";

    @Inject
    PhotoRepo _photoRepo;

    @BindView(R.id.btn_favorite)
    TextView _favoriteButton;
    @BindView(R.id.btn_hide)
    TextView _hideButton;
    @BindView(R.id.btn_like)
    TextView _likeButton;
    @BindView(R.id.btn_unlike)
    TextView _unlikeButton;

    private String _photoUrl;
    private Unbinder _unbinder;

    public static PhotoActionDialog newInstance(String photoUrl) {
        Bundle args = new Bundle();
        args.putString(KEY_PHOTO_URL, photoUrl);

        PhotoActionDialog dialog = new PhotoActionDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(KEY_PHOTO_URL)) {
            _photoUrl = args.getString(KEY_PHOTO_URL);
        } else if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PHOTO_URL)) {
            _photoUrl = savedInstanceState.getString(KEY_PHOTO_URL);
        }

        ((LikesApplication) getActivity().getApplication()).getMyComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_photo_menu, container, false);
        _unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        _favoriteButton.setOnClickListener(this::onFavoriteButtonClick);
        _likeButton.setOnClickListener(this::onLikeButtonClick);
        _unlikeButton.setOnClickListener(this::onUnlikeButtonClick);
        _hideButton.setOnClickListener(this::onHideButtonClick);
    }

    private void onHideButtonClick(View view) {
        Log.d(TAG, "onHideButtonClick: ");

        dismiss();
    }

    private void onUnlikeButtonClick(View view) {
        Log.d(TAG, "onUnlikeButtonClick: ");

        dismiss();
    }

    private void onLikeButtonClick(View view) {
        Log.d(TAG, "onLikeButtonClick: ");

        dismiss();
    }

    private void onFavoriteButtonClick(View view) {
        Log.d(TAG, "onFavoriteButtonClick: ");

        dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_PHOTO_URL, _photoUrl);
    }

    @Override
    public void onDestroy() {
        _favoriteButton.setOnClickListener(null);
        _likeButton.setOnClickListener(null);
        _unlikeButton.setOnClickListener(null);
        _hideButton.setOnClickListener(null);

        _unbinder.unbind();

        super.onDestroy();
    }
}
