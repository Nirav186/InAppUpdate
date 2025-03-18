package com.nirav.inapp.update

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Nirav Rangapariya on 18-Mar-25.
 */
@SuppressLint("StaticFieldLeak")
object InAppUpdateManager {

    private var activity: Activity? = null
    private var startFlow: () -> Unit = {}
    private lateinit var updateManager: AppUpdateManager
    private var appUpdateType = AppUpdateType.IMMEDIATE

    fun init(activity: Activity, isForceUpdate: Boolean = false, startFlow: () -> Unit) {
        this.activity = activity
        this.startFlow = startFlow
        this.appUpdateType = if (isForceUpdate) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE

        // Initialize updateManager
        updateManager = AppUpdateManagerFactory.create(activity)

        // Register listener for flexible updates
        if (appUpdateType == AppUpdateType.FLEXIBLE) {
            updateManager.registerListener(installStateUpdatedListener)
        }

        checkForUpdates()
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { installState ->
        when (installState.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                Toast.makeText(activity, "Download successful. Restarting app in 5 seconds.", Toast.LENGTH_LONG).show()
                (activity as? LifecycleOwner)?.lifecycleScope?.launch {
                    delay(5000)
                    updateManager.completeUpdate()
                }
            }
            InstallStatus.INSTALLED -> {
                startFlow()
            }
            else -> {
                Log.d("InAppUpdateManager", "Update status: ${installState.installStatus()}")
            }
        }
    }

    fun checkForUpdates() {
        updateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isAppAllowed = when (appUpdateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdateAvailable && isAppAllowed) {
                activity?.let {
                    updateManager.startUpdateFlowForResult(info, appUpdateType, it, 123)
                }
            } else {
                startFlow()
            }
        }.addOnFailureListener {
            startFlow()
            Log.e("InAppUpdateManager", "checkForUpdates failed: ${it.message}")
        }
    }

    fun resumeUpdate() {
        if (appUpdateType == AppUpdateType.IMMEDIATE) {
            updateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    activity?.let {
                        updateManager.startUpdateFlowForResult(info, appUpdateType, it, 123)
                    }
                }
            }
        }
    }

    fun handleResult(requestCode: Int, resultCode: Int) {
        if (requestCode == 123) {
            when (resultCode) {
                RESULT_CANCELED -> {
                    // Force close the app if update was canceled
                    activity?.finish()
                }
                RESULT_OK -> {
                    startFlow()
                }
                else -> {
                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun destroyUpdate() {
        if (::updateManager.isInitialized) {
            updateManager.unregisterListener(installStateUpdatedListener)
        }
        activity = null
    }
}