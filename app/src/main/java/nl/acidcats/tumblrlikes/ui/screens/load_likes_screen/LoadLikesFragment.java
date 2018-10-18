package nl.acidcats.tumblrlikes.ui.screens.load_likes_screen;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment;

/**
 * Created by stephan on 13/04/2017.
 */

public class LoadLikesFragment extends BaseFragment implements LoadLikesScreenContract.View {
    private static final String TAG = LoadLikesFragment.class.getSimpleName();

    @Inject
    LoadLikesScreenContract.Presenter _presenter;

    @BindView(R.id.tv_image_count)
    TextView _imageCountText;
    @BindView(R.id.tv_loading)
    TextView _loadingText;
    @BindView(R.id.spinner)
    ProgressBar _spinner;
    @BindView(R.id.btn_cancel)
    Button _cancelButton;

    public static LoadLikesFragment newInstance() {
        return new LoadLikesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loadlikes, container, false);
    }

    @Override
    protected void injectFrom(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _presenter.setView(this);
        _presenter.onViewCreated();

        if (getContext() != null) {
            _spinner.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }

        _cancelButton.setOnClickListener(v -> _presenter.cancelLoading());
    }

    @Override
    public void showLoadingCancelled() {
        _imageCountText.setText(R.string.loading_cancelled);

        _cancelButton.setEnabled(false);
    }

    @Override
    public void showLoadProgress(int pageCount, long totalPhotoCount) {
        _imageCountText.setText(getString(R.string.image_page_count, pageCount, totalPhotoCount));
    }

    @Override
    public void showAllLikesLoaded(long count) {
        _imageCountText.setText(getString(R.string.total_image_count, count));
        _loadingText.setText(R.string.all_loaded);
    }

    @Override
    public void showErrorAlert(int errorStringId) {
        if (getContext() != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error_title)
                    .setMessage(errorStringId)
                    .setPositiveButton(R.string.btn_retry, (dialog, which) -> _presenter.retryLoading())
                    .setNeutralButton(R.string.btn_settings, (dialog, which) -> _presenter.showSettings())
                    .setNegativeButton(R.string.btn_cancel, (dialog, which) -> _presenter.skipLoading())
                    .create()
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        _presenter.onDestroyView();

        super.onDestroyView();
    }
}
