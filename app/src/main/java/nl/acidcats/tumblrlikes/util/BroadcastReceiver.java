package nl.acidcats.tumblrlikes.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to listen for broadcasts, either from local or global broadcasts
 */
public class BroadcastReceiver extends android.content.BroadcastReceiver {
    private static final String TAG = BroadcastReceiver.class.getCanonicalName();

    protected IntentFilter _intentFilter;

    private boolean _useLocalBroadcastManager = true;
    private Context _context;
    private Map<String, ActionHandler> _handlerMap = new HashMap<>();
    private boolean _debug;
    private boolean _isRegistered;
    private boolean _isDestroyed;

    /**
     * Constructor
     *
     * @param useLocalBroadcastManager if true, this instance will listen to @link LocalBroadcastManager; otherwise the global Context broadcasts will be used
     */
    public BroadcastReceiver(Context context, boolean useLocalBroadcastManager) {
        this(context);

        _useLocalBroadcastManager = useLocalBroadcastManager;
    }

    /**
     * Constructor
     * Instances created through this will only listen to local Context broadcasts
     */
    public BroadcastReceiver(Context context) {
        _context = context;

        _intentFilter = new IntentFilter();
    }

    /**
     * Resume listening to broadcasts
     * Call this in your activity/fragment onResume
     */
    public void onResume() {
        if (_debug) Log.d(TAG, "onResume: ");

        if (_isDestroyed || _isRegistered || _intentFilter == null || _context == null) {
            Log.w(TAG, "onResume: called while not necessary, check the flow");
            return;
        }

        if (_useLocalBroadcastManager) {
            LocalBroadcastManager.getInstance(_context).registerReceiver(this, _intentFilter);
        } else {
            _context.registerReceiver(this, _intentFilter);
        }

        _isRegistered = true;
    }

    /**
     * Pause listening to broadcasts
     * Call this in your activity/fragment onPause
     */
    public void onPause() {
        if (_debug) Log.d(TAG, "onPause: ");

        unregisterReceiver();
    }

    private void unregisterReceiver() {
        if (_isDestroyed || !_isRegistered || _context == null) {
            Log.w(TAG, "unregisterReceiver: called while not necessary, check the flow");
            return;
        }

        if (_useLocalBroadcastManager) {
            LocalBroadcastManager.getInstance(_context).unregisterReceiver(this);
        } else {
            _context.unregisterReceiver(this);
        }

        _isRegistered = false;
    }

    /**
     * Listen for specified action broadcast
     *
     * @param action  action to listen for
     * @param handler ActionHandler instance which gets called when the specified action is received
     */
    public void addActionHandler(String action, ActionHandler handler) {
        if (_debug) Log.d(TAG, "addActionHandler: action = " + action);

        if (_handlerMap.containsKey(action)) {
            throw new Error("Action " + action + " already added to map");
        }

        _handlerMap.put(action, handler);

        _intentFilter.addAction(action);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (_debug) Log.d(TAG, "onReceive: action = " + action);

        for (Map.Entry<String, ActionHandler> entry : _handlerMap.entrySet()) {
            if (entry.getKey().equals(action)) {
                entry.getValue().onAction(action, intent);
                break;
            }
        }
    }

    public void onDestroy() {
        unregisterReceiver();

        _handlerMap.clear();
        _intentFilter = null;
        _context = null;

        _isDestroyed = true;
    }

    public interface ActionHandler {
        void onAction(String action, Intent intent);
    }

    public void setDebug(boolean isDebug) {
        _debug = isDebug;
    }
}
