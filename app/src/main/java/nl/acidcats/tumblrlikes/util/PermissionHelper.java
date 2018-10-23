package nl.acidcats.tumblrlikes.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by stephan on 7-10-2015.
 */
public class PermissionHelper {
    private static final String TAG = PermissionHelper.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 255;

    public static final int GRANTED = 0;
    public static final int DENIED = 1;
    public static final int BLOCKED = 2;

    private static boolean sDebug;
    private ArrayList<PermissionListener> _listeners;

    public static void setDebug(boolean value) {
        sDebug = value;
    }

    public enum Permission {
        READ_CALENDAR(Manifest.permission.READ_CALENDAR),
        WRITE_CALENDAR(Manifest.permission.WRITE_CALENDAR),

        CAMERA(Manifest.permission.CAMERA),

        READ_CONTACTS(Manifest.permission.READ_CONTACTS),
        WRITE_CONTACTS(Manifest.permission.WRITE_CONTACTS),
        GET_ACCOUNTS(Manifest.permission.GET_ACCOUNTS),

        ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
        ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),

        RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),

        READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE),
        CALL_PHONE(Manifest.permission.CALL_PHONE),
        READ_CALL_LOG(Manifest.permission.READ_CALL_LOG),
        WRITE_CALL_LOG(Manifest.permission.WRITE_CALL_LOG),
        ADD_VOICEMAIL(Manifest.permission.ADD_VOICEMAIL),
        USE_SIP(Manifest.permission.USE_SIP),
        PROCESS_OUTGOING_CALLS(Manifest.permission.PROCESS_OUTGOING_CALLS),

        // This may only be added from API 20+
//        BODY_SENSORS(Manifest.permission.BODY_SENSORS),

        SEND_SMS(Manifest.permission.SEND_SMS),
        RECEIVE_SMS(Manifest.permission.RECEIVE_SMS),
        READ_SMS(Manifest.permission.READ_SMS),
        RECEIVE_WAP_PUSH(Manifest.permission.RECEIVE_WAP_PUSH),
        RECEIVE_MMS(Manifest.permission.RECEIVE_MMS),

        READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
        WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        private String _androidPermission;

        Permission(String androidPermission) {
            _androidPermission = androidPermission;
        }

        private String getAndroidPermission() {
            return _androidPermission;
        }

        @Nullable
        private static Permission getPermission(String androidPermission) {
            for (Permission permission : Permission.values()) {
                if (permission.getAndroidPermission().equals(androidPermission)) {
                    return permission;
                }
            }
            return null;
        }
    }

    private static List<Permission> sManifestPermissions;
    private static PermissionHelper sInstance = new PermissionHelper();


    private PermissionHelper() {
    }

    public static PermissionHelper getInstance() {
        return sInstance;
    }

    public boolean hasPermission(Context context, Permission permission) {
        if (sDebug) Log.d(TAG, "hasPermission: " + permission);

        checkPermissionInManifest(context, permission);

        boolean isGranted = ContextCompat.checkSelfPermission(context, permission.getAndroidPermission()) == PackageManager.PERMISSION_GRANTED;
        if (sDebug) Log.d(TAG, "hasPermission: isGranted = " + isGranted);

        return isGranted;
    }

    public int getPermissionStatus(Activity activity, Permission permission) {
        checkPermissionInManifest(activity, permission);

        if (ContextCompat.checkSelfPermission(activity, permission.getAndroidPermission()) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.getAndroidPermission())) {
                return BLOCKED;
            }

            return DENIED;
        }

        return GRANTED;
    }

    public void requestPermission(Activity activity, Permission permission, String rationaleMessage) {
        if (sDebug) Log.d(TAG, "requestPermission: " + permission);

        requestPermissions(activity, new Permission[]{permission}, rationaleMessage);
    }

    public void requestPermissions(Activity activity, Permission[] permissions, String rationaleMessage) {
        if (sDebug) Log.d(TAG, "requestPermissions: " + Arrays.toString(permissions));

        boolean shouldShowRationale = false;
        List<Permission> remainingPermissions = new ArrayList<>();
        for (Permission permission : permissions) {
            if (hasPermission(activity, permission)) {
                notifyListeners(permission, true);
            } else {
                remainingPermissions.add(permission);
                shouldShowRationale = shouldShowRationale || ActivityCompat.shouldShowRequestPermissionRationale(activity, permission
                        .getAndroidPermission());
            }
        }

        if (!remainingPermissions.isEmpty()) {
            String[] androidPermissions = new String[remainingPermissions.size()];
            for (int i = 0; i < remainingPermissions.size(); i++) {
                androidPermissions[i] = remainingPermissions.get(i).getAndroidPermission();
            }

            ActivityCompat.requestPermissions(activity, androidPermissions, REQUEST_PERMISSIONS);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (sDebug) Log.d(TAG, "onRequestPermissionsResult: requestCode = " + requestCode
                + ", permissions = " + Arrays.toString(permissions)
                + ", grantResults = " + Arrays.toString(grantResults));

        if (requestCode != REQUEST_PERMISSIONS) {
            if (sDebug) Log.d(TAG, "onRequestPermissionsResult: not meant for me");
            return;
        }

        int index = 0;
        for (String androidPermission : permissions) {
            Permission permission = Permission.getPermission(androidPermission);

            if (permission != null) {
                boolean isGranted = (grantResults[index] == PackageManager.PERMISSION_GRANTED);
                if (sDebug)
                    Log.d(TAG, "onRequestPermissionsResult: permission " + permission + " granted: " + isGranted);

                notifyListeners(permission, isGranted);
            }

            index++;
        }
    }

    private void notifyListeners(Permission permission, boolean isGranted) {
        if (_listeners != null) {
            // run through copy of list of listeners since the list might change while we're looping
            List<PermissionListener> listeners = new ArrayList<>(_listeners);
            for (PermissionListener listener : listeners) {
                listener.onPermissionResult(permission, isGranted);
            }
        }
    }

    private static void checkPermissionInManifest(Context context, Permission permission) {
        if (sManifestPermissions == null) {
            readManifestPermissions(context);
        }

        boolean manifestContainsPermission = sManifestPermissions.contains(permission);
        if (sDebug)
            Log.d(TAG, "checkPermissionInManifest: manifestContainsPermission = " + manifestContainsPermission);

        if (!manifestContainsPermission) {
            throw new RuntimeException("permission " + permission.getAndroidPermission() + " not found, add to Manifest");
        }
    }

    private static void readManifestPermissions(Context context) {
        sManifestPermissions = new ArrayList<>();

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);

            if (sDebug)
                Log.d(TAG, "readManifestPermissions: requestedPermissions = " + Arrays.toString(packageInfo.requestedPermissions));

            for (String androidPermission : packageInfo.requestedPermissions) {
                Permission permission = Permission.getPermission(androidPermission);
                if (permission != null) {
                    sManifestPermissions.add(permission);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "checkPermissions: couldn't retrieve permissions" + e);
        }
    }

    public interface PermissionListener {
        void onPermissionResult(Permission permission, boolean isGranted);
    }

    public void addPermissionListener(PermissionListener listener) {
        if (_listeners == null) {
            _listeners = new ArrayList<>();
        }
        _listeners.add(listener);
    }

    public void removePermissionListener(PermissionListener listener) {
        if (_listeners != null) {
            _listeners.remove(listener);
        }
    }
}
