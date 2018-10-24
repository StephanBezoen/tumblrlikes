package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.filterdropdown;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import nl.acidcats.tumblrlikes.ui.screens.photo_screen.constants.Filter;

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
        for (Filter filter : Filter.values()) {
            _filterOptionViews.add(new FilterOptionView(this, filter));
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
            filterOptionView.onDestroyView();
        }
    }
}
