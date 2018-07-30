package nl.acidcats.tumblrlikes.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.di.AppComponent;

/**
 * Created on 30/07/2018.
 */
public abstract class BaseFragment extends Fragment {

    private Unbinder _unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity().getApplication() != null) {
            injectFrom(((LikesApplication) getActivity().getApplication()).getAppComponent());
        }
    }

    abstract protected void injectFrom(AppComponent appComponent);

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        _unbinder = ButterKnife.bind(this, view);
    }

    protected void sendBroadcast(String action) {
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(action));
        }
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        _unbinder.unbind();

        super.onDestroyView();
    }
}
