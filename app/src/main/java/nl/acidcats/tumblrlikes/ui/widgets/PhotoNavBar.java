package nl.acidcats.tumblrlikes.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.constants.Broadcasts;

/**
 * Created by stephan on 16/05/2017.
 */

public class PhotoNavBar extends FrameLayout {
    private static final String TAG = PhotoNavBar.class.getSimpleName();

    @BindView(R.id.btn_filter)
    TextView _filterButton;
    @BindView(R.id.btn_settings)
    View _settingsButton;
    @BindView(R.id.btn_refresh)
    View _refreshButton;

    private Unbinder _unbinder;

    public PhotoNavBar(@NonNull Context context) {
        super(context);

        init();
    }

    public PhotoNavBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PhotoNavBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.navbar, this, true);
        _unbinder = ButterKnife.bind(this, view);

        _filterButton.setOnClickListener(this::onFilterButtonClick);
        _settingsButton.setOnClickListener(this::onSettingsButtonClick);
        _refreshButton.setOnClickListener(this::onRefreshButtonClick);

        _filterButton.setText(R.string.filter_all);

        hide();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    private void onRefreshButtonClick(View view) {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Broadcasts.REFRESH_REQUEST));
    }

    private void onSettingsButtonClick(View view) {

    }

    private void onFilterButtonClick(View view) {

    }

    public void onDestroy() {
        _filterButton.setOnClickListener(null);
        _settingsButton.setOnClickListener(null);
        _refreshButton.setOnClickListener(null);

        _unbinder.unbind();
    }
}
