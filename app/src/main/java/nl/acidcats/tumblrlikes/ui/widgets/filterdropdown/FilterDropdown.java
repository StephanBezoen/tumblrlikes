package nl.acidcats.tumblrlikes.ui.widgets.filterdropdown;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import nl.acidcats.tumblrlikes.data.constants.FilterType;

/**
 * Created by stephan on 18/05/2017.
 */

public class FilterDropdown extends LinearLayout {
    private static final String TAG = FilterDropdown.class.getSimpleName();

    private List<FilterOptionView> _filterOptionViews = new ArrayList<>();

    public FilterDropdown(Context context) {
        super(context);

        init();
    }

    public FilterDropdown(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public FilterDropdown(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void init() {
        for (FilterType filterType : FilterType.values()) {
            _filterOptionViews.add(new FilterOptionView(this, filterType));
        }

        if (!isInEditMode()) {
            hide();
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void setFilterOptionSelectionListener(FilterOptionSelectionListener listener) {
        for (FilterOptionView filterOptionView : _filterOptionViews) {
            filterOptionView.setFilterOptionSelectionListener(listener);
        }
    }

    public void onDestroy() {
        for (FilterOptionView filterOptionView : _filterOptionViews) {
            filterOptionView.onDestroy();
        }
    }
}
