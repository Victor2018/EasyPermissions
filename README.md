# EasyPermissions

Kotlin version of the popular [googlesample/easypermissions](https://github.com/googlesamples/easypermissions) wrapper library to simplify basic system
permissions logic on Android M(6) to T(33).

[![Logo](art/logo.png)](https://www.youtube.com/watch?v=51fX94dU7Og)

This library lifts the burden that comes with writing a bunch of check statements whether a permission has been granted or not from you, in order to keep your code clean and safe.

## Installation

EasyPermissions-ktx is installed by adding the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation 'com.github.Victor2018:easypermissions:latestVersion'
}
```

## Usage

### Basic

To begin using EasyPermissions-ktx, have your `Activity` (or `Fragment`) override the `onRequestPermissionsResult` method:

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
```

### Request Permissions

The example below shows how to request permissions for a method that requires both
`CAMERA` and `ACCESS_FINE_LOCATION` permissions. There are a few things to note:

* Using `EasyPermissions#hasPermissions(...)` to check if the app already has the
  required permissions. This method can take any number of permissions as its final
  argument.
* Requesting permissions with `EasyPermissions#requestPermissions`. This method
  will request the system permissions and show the rationale string provided if
  necessary. The request code provided should be unique to this request, and the method
  can take any number of permissions as its final argument.
* Use of the `AfterPermissionGranted` annotation. This is optional, but provided for
  convenience. If all of the permissions in a given request are granted, *all* methods
  annotated with the proper request code will be executed(be sure to have an unique request code). The annotated method needs to be *void* and *without input parameters* (instead, you can use *onSaveInstanceState* in order to keep the state of your suppressed parameters). This is to simplify the common
  flow of needing to run the requesting method after all of its permissions have been granted.
  This can also be achieved by adding logic on the `onPermissionsGranted` callback.

```kotlin
@AfterPermissionGranted(REQUEST_CODE_LOCATION_AND_CONTACTS_PERMISSION)
private void methodRequiresTwoPermission() {
    if (EasyPermissions.hasPermissions(this, ACCESS_FINE_LOCATION, READ_CONTACTS)) {
        // Already have permission, do the thing
        // ...
    } else {
        // Do not have permissions, request them now
        EasyPermissions.requestPermissions(
            host = this,
            rationale = getString(R.string.permission_location_and_contacts_rationale_message),
            requestCode = REQUEST_CODE_LOCATION_AND_CONTACTS_PERMISSION,
            perms = ACCESS_FINE_LOCATION, READ_CONTACTS
        )
    }
}
```

Or for finer control over the rationale dialog, use a `PermissionRequest`:

```kotlin
val request = PermissionRequest.Builder(spyActivity)
    .code(REQUEST_CODE)
    .perms(REQUEST_CODE_LOCATION_AND_CONTACTS_PERMISSION)
    .theme(R.style.my_fancy_style)
    .rationale(R.string.camera_and_location_rationale)
    .positiveButtonText(R.string.rationale_ask_ok)
    .negativeButtonText(R.string.rationale_ask_cancel)
    .build()
EasyPermissions.requestPermissions(spyActivity, request)
```

Optionally, for a finer control, you can have your `Activity` / `Fragment` implement
the `PermissionCallbacks` interface.

```kotlin
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // Some permissions have been granted
        // ...
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // Some permissions have been denied
        // ...
    }
}
```

### Required Permissions

In some cases your app will not function properly without certain permissions. If the user
denies these permissions with the "Never Ask Again" option, you will be unable to request
these permissions from the user and they must be changed in app settings. You can use the
method `EasyPermissions.somePermissionPermanentlyDenied(...)` to display a dialog to the
user in this situation and direct them to the system setting screen for your app:

**Note**: Due to a limitation in the information provided by the Android
framework permissions API, the `somePermissionPermanentlyDenied` method only
works after the permission has been denied and your app has received
the `onPermissionsDenied` callback. Otherwise the library cannot distinguish
permanent denial from the "not yet denied" case.

```kotlin
override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
    Log.d(TAG, "onPermissionsDenied: $requestCode :${perms.size()}")

    // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
    // This will display a dialog directing them to enable the permission in app settings.
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
        SettingsDialog.Builder(this).build().show()
    }
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
                if (hasStoragePermission()) yes else no
            ),
            LENGTH_LONG
        ).show()
    }
}
```

### Interacting with the rationale dialog

Implement the `EasyPermissions.RationaleCallbacks` if you want to interact with the rationale dialog.

```kotlin
override fun onRationaleAccepted(requestCode: Int) {
    // Rationale accepted to request some permissions
    // ...
}

override fun onRationaleDenied(requestCode: Int) {
    // Rationale denied to request some permissions
    // ...
}
```

### StoragePermission fast use
- check storage permission
```
EasyPermissions.hasStoragePermission(context: Context?)
```
- request storage permission
```
EasyPermissions.requestStoragePermission(host: Activity, rationale: String, requestCode: Int)
EasyPermissions.requestStoragePermission(host: Fragment,rationale: String,requestCode: Int)
```

Rationale callbacks don't necessarily imply permission changes. To check for those, see the `EasyPermissions.PermissionCallbacks`.

## License

Copyright (c) 2024 Victor

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
