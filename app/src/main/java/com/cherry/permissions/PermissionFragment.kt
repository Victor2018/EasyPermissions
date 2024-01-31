package com.cherry.permissions

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cherry.permissions.PermissionRequestCode.REQUEST_CODE_RECORD_PERMISSION
import com.cherry.permissions.databinding.FragmentPermissionBinding
import com.cherry.permissions.lib.EasyPermissions
import com.cherry.permissions.lib.annotations.AfterPermissionGranted
import com.cherry.permissions.lib.dialogs.SettingsDialog
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PermissionFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val TAG = "SecondFragment"
    private var _binding: FragmentPermissionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mBtnRecord.setOnClickListener {
            requestRecordPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    // ============================================================================================
    //  Implementation Permission Callbacks
    // ============================================================================================

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(TAG, getString(R.string.log_permissions_granted, requestCode, perms.size))
        //会回调 AfterPermissionGranted注解对应方法
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(TAG, getString(R.string.log_permissions_denied, requestCode, perms.size))
        val settingsDialogBuilder = SettingsDialog.Builder(requireContext())

        when(requestCode) {
            REQUEST_CODE_RECORD_PERMISSION -> {
                settingsDialogBuilder.title = getString(
                    com.cherry.permissions.lib.R.string.title_settings_dialog,
                    "Record Permission")
                settingsDialogBuilder.rationale = getString(
                    com.cherry.permissions.lib.R.string.rationale_ask_again,
                    "Record Permission")
            }
        }

        settingsDialogBuilder.build().show()
    }

    // ============================================================================================
    //  Private Methods
    // ============================================================================================

    @AfterPermissionGranted(REQUEST_CODE_RECORD_PERMISSION)
    private fun requestRecordPermission() {
        if (EasyPermissions.hasPermissions(context, Manifest.permission.RECORD_AUDIO)) {
            // Have permission, do the thing!
            showMessage(binding.root,"AfterPermissionGranted you have Record permission,you can record audio.")
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_sms_rationale_message),
                REQUEST_CODE_RECORD_PERMISSION,
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    fun showMessage(view: View,message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}