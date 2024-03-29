/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cherry.permissions.lib.helpers.base

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cherry.permissions.lib.helpers.ActivityPermissionsHelper
import com.cherry.permissions.lib.helpers.AppCompatActivityPermissionsHelper
import com.cherry.permissions.lib.helpers.FragmentPermissionsHelper
import com.cherry.permissions.lib.models.PermissionRequest

/**
 * Delegate class to make permission calls based on the 'host' (Fragment, Activity, etc).
 */
abstract class PermissionsHelper<T>(val host: T) {
    private val TAG = javaClass.simpleName

    companion object {

        fun newInstance(host: Activity): PermissionsHelper<out Activity> {
            return (host as? AppCompatActivity)?.let {
                AppCompatActivityPermissionsHelper(it)
            } ?: ActivityPermissionsHelper(host)
        }

        fun newInstance(host: Fragment): PermissionsHelper<Fragment> {
            return FragmentPermissionsHelper(host)
        }
    }

    // ============================================================================================
    // Public abstract methods
    // ============================================================================================

    abstract var context: Context?

    abstract fun directRequestPermissions(requestCode: Int, perms: Array<out String>)

    abstract fun shouldShowRequestPermissionRationale(perm: String): Boolean

    abstract fun showRequestPermissionRationale(permissionRequest: PermissionRequest)

    // ============================================================================================
    //  Public methods
    // ============================================================================================

    fun requestPermissions(permissionRequest: PermissionRequest) {
        Log.e(TAG,"requestPermissions()......")
        if (shouldShowRationale(permissionRequest.perms)) {
            Log.e(TAG,"requestPermissions()......shouldShowRationale")
            showRequestPermissionRationale(permissionRequest)
        } else {
            Log.e(TAG,"requestPermissions()......directRequestPermissions")
            directRequestPermissions(permissionRequest.code, permissionRequest.perms)
        }
    }

    fun somePermissionPermanentlyDenied(perms: List<String>): Boolean {
        return perms.any { permissionPermanentlyDenied(it) }
    }

    fun permissionPermanentlyDenied(perm: String): Boolean {
        return !shouldShowRequestPermissionRationale(perm)
    }

    fun somePermissionDenied(perms: Array<out String>): Boolean {
        return shouldShowRationale(perms)
    }

    // ============================================================================================
    //  Private methods
    // ============================================================================================

    private fun shouldShowRationale(perms: Array<out String>): Boolean {
        return perms.any { shouldShowRequestPermissionRationale(it) }
    }
}
