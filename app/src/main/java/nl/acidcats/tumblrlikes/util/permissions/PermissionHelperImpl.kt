package nl.acidcats.tumblrlikes.util.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import nl.acidcats.tumblrlikes.util.permissions.PermissionHelper.Permission
import nl.acidcats.tumblrlikes.util.permissions.PermissionHelper.PermissionStatus

class PermissionHelperImpl : PermissionHelper {

    companion object {
        const val REQUEST_PERMISSIONS = 255

        private val sManifestPermissions: MutableList<PermissionHelper.Permission> = ArrayList()
        private var sHasReadManifest = false

        private fun readManifestPermissions(context: Context) {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            for (requestedPermission in packageInfo.requestedPermissions) {
                val permission = PermissionHelper.Permission.getPermission(requestedPermission)
                if (permission != null) {
                    sManifestPermissions += permission
                }
            }

            sHasReadManifest = true
        }

        fun checkPermissionInManifest(context: Context, permission: Permission) {
            if (!sHasReadManifest) {
                readManifestPermissions(context)
            }

            if (!sManifestPermissions.contains(permission)) {
                throw RuntimeException("Permission ${permission.androidPermission} not found, add to Manifest")
            }
        }
    }

    private val permissionListeners: MutableList<PermissionListener> = ArrayList()

    override fun hasPermission(context: Context, permission: Permission): Boolean {
        checkPermissionInManifest(context, permission)

        return ContextCompat.checkSelfPermission(context, permission.androidPermission) == PackageManager.PERMISSION_GRANTED
    }

    override fun getPermissionStatus(activity: Activity, permission: Permission): PermissionStatus {
        checkPermissionInManifest(activity, permission)

        if (ContextCompat.checkSelfPermission(activity, permission.androidPermission) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.androidPermission)) {
                return PermissionStatus.BLOCKED
            }

            return PermissionStatus.DENIED
        }

        return PermissionStatus.GRANTED
    }

    override fun requestPermission(activity: Activity, permission: Permission, rationale: String) {
        requestPermissions(activity, listOf(permission), rationale)
    }

    override fun requestPermissions(activity: Activity, permissions: List<Permission>, rationale: String) {
        var shouldShowRationale = false

        val remainingPermissions: MutableList<Permission> = ArrayList()

        for (permission in permissions) {
            if (hasPermission(activity, permission)) {
                notifyListeners(permission, true)
            } else {
                remainingPermissions += permission

                shouldShowRationale = shouldShowRationale || ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.androidPermission)
            }
        }

        if (!remainingPermissions.isEmpty()) {
            val androidPermissions: MutableList<String> = ArrayList()

            for (remainingPermission in remainingPermissions) {
                androidPermissions += remainingPermission.androidPermission
            }

            ActivityCompat.requestPermissions(activity, androidPermissions.toTypedArray(), REQUEST_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != REQUEST_PERMISSIONS) return

        permissions.forEachIndexed { index, androidPermission ->
            val permission = Permission.getPermission(androidPermission)
            if (permission != null) {
                notifyListeners(permission, (grantResults[index] == PackageManager.PERMISSION_GRANTED))
            }
        }
    }

    override fun addPermissionListener(listener: PermissionListener) {
        permissionListeners += listener
    }

    override fun removePermissionListener(listener: PermissionListener) {
        permissionListeners -= listener
    }

    private fun notifyListeners(permission: Permission, isGranted: Boolean) {
        for (permissionListener in permissionListeners) {
            permissionListener.invoke(permission, isGranted)
        }
    }
}