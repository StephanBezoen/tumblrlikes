package nl.acidcats.tumblrlikes.testmoshi;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.acidcats.tumblrlikes.data.repo.app.AppRepo;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepo;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrLikeVO;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Created on 10/07/2018.
 */
public class TestActivity extends AppCompatActivity {
    private static final String TAG = TestActivity.class.getSimpleName();

    private static final int PAGE_COUNT = 10;

    @Inject
    LikesRepo _likesRepo;
    @Inject
    AppRepo _appRepo;

    @BindView(R.id.btn_start_loading)
    TextView _startLoadingButton;

    private int _loadedPageCount;
    private int _processedPageCount;
    private long _startTime;
    BehaviorSubject<List<TumblrLikeVO>> _storeDataSubject = BehaviorSubject.create();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_moshi);
        ((TestMoshiApplication) getApplication()).getMyComponent().inject(this);
        ButterKnife.bind(this);

        _appRepo.setTumblrBlog(BuildConfig.BLOG + ".tumblr.com");

        _storeDataSubject
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(this::onTumblrLikesReceived);

        resetStartButton();
    }

    private void startLoading() {
        _startLoadingButton.setOnClickListener(null);
        _startLoadingButton.setText(R.string.loading);

        _startTime = SystemClock.elapsedRealtime();

        _loadedPageCount = 0;
        loadLikesPage(new Date().getTime());
    }

    private void loadLikesPage(long time) {
        Log.d(TAG, "loadLikesPage: ");

        _likesRepo
                .getLikes(_appRepo.getTumblrBlog(), 20, time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLikesLoaded, this::onLoadError);
    }

    private void onLoadError(Throwable throwable) {
        Log.e(TAG, "onLoadError: ");
    }

    private void onLikesLoaded(List<TumblrLikeVO> likes) {
        Log.d(TAG, "onLikesLoaded: " + likes.size());

        _loadedPageCount++;

        _storeDataSubject.onNext(likes);

        if (!_likesRepo.hasMoreLikes() || _loadedPageCount >= PAGE_COUNT) {
            Log.d(TAG, "onLikesLoaded: " + _loadedPageCount + " pages loaded, time taken: " + (SystemClock.elapsedRealtime() - _startTime) + " ms");
            _startLoadingButton.setText(R.string.done);
            new Handler().postDelayed(this::resetStartButton, 1500);
        } else {
            loadLikesPage(_likesRepo.getLastLikeTime());
        }
    }

    private void resetStartButton() {
        _startLoadingButton.setText(R.string.btn_start_loading);
        _startLoadingButton.setOnClickListener(v -> startLoading());
    }

    private void onTumblrLikesReceived(List<TumblrLikeVO> tumblrLikeVOS) {
        _processedPageCount++;

        Log.d(TAG, "onTumblrLikesReceived: " + _processedPageCount);

    }
}
