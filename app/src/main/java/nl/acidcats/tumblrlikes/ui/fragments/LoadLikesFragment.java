package nl.acidcats.tumblrlikes.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.constants.Broadcasts;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepo;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.usecase.GetLikesPageUseCase;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;

/**
 * Created by stephan on 13/04/2017.
 */

public class LoadLikesFragment extends Fragment {
    private static final String TAG = LoadLikesFragment.class.getSimpleName();

    @Inject
    LikesRepo _likesRepo;
    @Inject
    PhotoRepo _photoRepo;
    @Inject
    GetLikesPageUseCase _likesPageUseCase;

    @BindView(R.id.tv_image_count)
    TextView _imageCountText;
    @BindView(R.id.tv_loading)
    TextView _loadingText;

    private int _pageCount;
    private Unbinder _unbinder;

    public static LoadLikesFragment newInstance() {
        return new LoadLikesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((LikesApplication) getActivity().getApplication()).getMyComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loadlikes, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loadLikesPage(new Date().getTime());
    }

    private void loadLikesPage(long time) {
        _likesPageUseCase
                .getPageOfLikesBefore(time)
                .subscribe(
                        this::handleLikesPageLoaded,
                        throwable -> Log.e(TAG, "onStart: " + throwable.getMessage())
                );
    }

    private void handleLikesPageLoaded(List<PhotoEntity> photoEntities) {
        _pageCount++;

        long count = _photoRepo.getPhotoCount();

        _imageCountText.setText(getString(R.string.image_page_count, _pageCount, count));

        if (_likesRepo.hasMoreLikes()) {
            loadLikesPage(_likesRepo.getLastLikeTime());
        } else {
            _imageCountText.setText(getString(R.string.total_image_count, count));
            _loadingText.setText(R.string.all_loaded);

            _likesRepo.setCheckComplete();

            new Handler().postDelayed(this::notifyComplete, 500);
        }
    }

    private void notifyComplete() {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Broadcasts.ALL_LIKES_LOADED));
    }

    @Override
    public void onDestroy() {
        if (_unbinder != null) {
            _unbinder.unbind();
            _unbinder = null;
        }

        super.onDestroy();
    }
}
