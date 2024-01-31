package com.cherry.permissions

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.cherry.permissions.PermissionRequestCode.REQUEST_CODE_CAMERA_PERMISSION
import com.cherry.permissions.PermissionRequestCode.REQUEST_CODE_LOCATION_AND_CONTACTS_PERMISSION
import com.cherry.permissions.PermissionRequestCode.REQUEST_CODE_STORAGE_PERMISSION
import com.cherry.permissions.databinding.ActivityMainBinding
import com.cherry.permissions.lib.EasyPermissions
import com.cherry.permissions.lib.EasyPermissions.hasStoragePermission
import com.cherry.permissions.lib.annotations.AfterPermissionGranted
import com.cherry.permissions.lib.dialogs.DEFAULT_SETTINGS_REQ_CODE
import com.cherry.permissions.lib.dialogs.SettingsDialog

class MainActivity : AppCompatActivity(),OnClickListener, EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    var TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_use_fragment -> {
                startActivity(Intent(this,UseInFragmentActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun initView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener(this)
        binding.mMainContent.mBtnStorage.setOnClickListener(this)
        binding.mMainContent.mBtnLocationAndContacts.setOnClickListener(this)
        binding.mMainContent.mBtnCamera.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v?.id) {
            R.id.fab -> {
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            R.id.mBtnStorage -> {
                requestStoragePermission(v)
            }
            R.id.mBtnLocationAndContacts -> {
                requestPermissionLocationAndContacts(v)
            }
            R.id.mBtnCamera -> {
                requestPermissionCamera(v)
            }
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_CAMERA_PERMISSION)
    private fun requestPermissionCamera(view: View) {
        if (hasCameraPermission()) {
            // Have permission, do things!
            showMessage(view,"you have Camera permission,you can take photo")
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_camera_rationale_message),
                REQUEST_CODE_CAMERA_PERMISSION,
                Manifest.permission.CAMERA
            )
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_LOCATION_AND_CONTACTS_PERMISSION)
    private fun requestPermissionLocationAndContacts(view: View) {
        if (hasLocationAndContactsPermissions()) {
            // Have permissions, do things!
            showMessage(view,"you have Location and Contacts permissions,you can Location and get Contacts")
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_location_and_contacts_rationale_message),
                REQUEST_CODE_LOCATION_AND_CONTACTS_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS
            )
        }
    }


    @AfterPermissionGranted(REQUEST_CODE_STORAGE_PERMISSION)
    private fun requestStoragePermission(view: View) {
        if (hasStoragePermission(this)) {
            // Have permission, do things!
            showMessage(view,"you have Storage permission,you can storage things")
        } else {
            // Ask for one permission
            EasyPermissions.requestStoragePermission(
                this,
                getString(R.string.permission_storage_rationale_message),
                REQUEST_CODE_STORAGE_PERMISSION
            )
        }
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    private fun hasLocationAndContactsPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
        )
    }

    private fun hasSmsPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_SMS)
    }

    fun showMessage(view: View,message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DEFAULT_SETTINGS_REQ_CODE) {
            val yes = getString(R.string.yes)
            val no = getString(R.string.no)

            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                this,
                getString(
                    R.string.returned_from_app_settings_to_activity,
                    if (hasCameraPermission()) yes else no,
                    if (hasLocationAndContactsPermissions()) yes else no,
                    if (hasSmsPermission()) yes else no,
                    if (hasStoragePermission(this)) yes else no
                ),
                Toast.LENGTH_LONG
            ).show()
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
        when(requestCode) {
            REQUEST_CODE_STORAGE_PERMISSION -> {
                showMessage(binding.root,"you have Storage permission,you can storage things")
            }
            REQUEST_CODE_LOCATION_AND_CONTACTS_PERMISSION -> {
                showMessage(binding.root,"you have Location and Contacts permissions,you can Location and get Contacts")
            }
            REQUEST_CODE_CAMERA_PERMISSION -> {
                showMessage(binding.root,"you have Camera permission,you can take photo")
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(TAG, getString(R.string.log_permissions_denied, requestCode, perms.size))

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            val settingsDialogBuilder = SettingsDialog.Builder(this)

            when(requestCode) {
                REQUEST_CODE_STORAGE_PERMISSION -> {
                    settingsDialogBuilder.title = getString(
                        com.cherry.permissions.lib.R.string.title_settings_dialog,
                        "Storage Permission")
                    settingsDialogBuilder.rationale = getString(
                        com.cherry.permissions.lib.R.string.rationale_ask_again,
                        "Storage Permission")
                }
                REQUEST_CODE_LOCATION_AND_CONTACTS_PERMISSION -> {
                    settingsDialogBuilder.title = getString(
                        com.cherry.permissions.lib.R.string.title_settings_dialog,
                        "Location and Contacts Permissions")
                    settingsDialogBuilder.rationale = getString(
                        com.cherry.permissions.lib.R.string.rationale_ask_again,
                        "Location and Contacts Permissions")
                }
                REQUEST_CODE_CAMERA_PERMISSION -> {
                    settingsDialogBuilder.title = getString(
                        com.cherry.permissions.lib.R.string.title_settings_dialog,
                        "Camera Permission")
                    settingsDialogBuilder.rationale = getString(
                        com.cherry.permissions.lib.R.string.rationale_ask_again,
                        "Camera Permission")
                }
            }

            settingsDialogBuilder.build().show()
        }

    }

    // ============================================================================================
    //  Implementation Rationale Callbacks
    // ============================================================================================

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(TAG, getString(R.string.log_permission_rationale_accepted, requestCode))
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(TAG, getString(R.string.log_permission_rationale_denied, requestCode))
    }
}