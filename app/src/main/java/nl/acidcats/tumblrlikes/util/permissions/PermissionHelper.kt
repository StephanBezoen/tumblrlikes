package nl.acidcats.tumblrlikes.util.permissions

import android.Manifest
import android.app.Activity
import android.content.Context

/**
 * Created on 05/11/2018.
 */
interface PermissionHelper {
    enum class PermissionStatus {
        GRANTED, DENIED, BLOCKED
    }

    enum class Permission(val androidPermission: String) {
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

        BODY_SENSORS(Manifest.permission.BODY_SENSORS),

        SEND_SMS(Manifest.permission.SEND_SMS),
        RECEIVE_SMS(Manifest.permission.RECEIVE_SMS),
        READ_SMS(Manifest.permission.READ_SMS),
        RECEIVE_WAP_PUSH(Manifest.permission.RECEIVE_WAP_PUSH),
        RECEIVE_MMS(Manifest.permission.RECEIVE_MMS),

        USE_FINGERPRINT(Manifest.permission.USE_FINGERPRINT),

        READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
        WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        companion object {
            fun getPermission(androidPermission: String): Permission? {
                for (permission in Permission.values()) {
                    if (permission.androidPermission == androidPermission) {
                        return permission
                    }
                }
                return null
            }
        }
    }

    fun hasPermission(context: Context, permission: Permission): Boolean
    fun getPermissionStatus(activity: Activity, permission: Permission): PermissionStatus

    fun requestPermission(activity: Activity, permission: Permission, rationale: String)
    fun requestPermissions(activity: Activity, permissions: List<Permission>, rationale: String)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)

    fun addPermissionListener(listener: PermissionListener)
    fun removePermissionListener(listener: PermissionListener)
}