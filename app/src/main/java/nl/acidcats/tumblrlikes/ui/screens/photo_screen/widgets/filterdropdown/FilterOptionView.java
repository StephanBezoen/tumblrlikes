package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.filterdropdown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.constants.Filter;

/**
 * Created by stephan on 18/05/2017.
 */

class FilterOptionView {
    private static final String TAG = FilterOptionView.class.getSimpleName();

    @BindView(R.id.tv_filteroption)
    TextView _filterOptionText;

    private final Unbinder _unbinder;
    private final Filter _filter;
    private FilterOptionSelectionListener _filterOptionSelectionListener;
    private final View _view;

    FilterOptionView(ViewGroup parent, Filter filter) {
        _filter = filter;

        Context context = parent.getContext();

        _view = LayoutInflater.from(context).inflate(R.layout.listitem_filteroption, parent, false);
        _unbinder = ButterKnife.bind(this, _view);

        _filterOptionText.setText(context.getString(filter.getResId()));

        _view.setOnClickListener(this::onViewClick);

        parent.addView(_view);
    }

    private void onViewClick(View v) {
        if (_filterOptionSelectionListener != null) {
            _filterOptionSelectionListener.onOptionSelected(_filter);
        }
    }

    void setFilterOptionSelectionListener(FilterOptionSelectionListener listener) {
        _filterOptionSelectionListener = listener;
    }

    void onDestroyView() {
        _view.setOnClickListener(null);

        _unbinder.unbind();
    }
}
