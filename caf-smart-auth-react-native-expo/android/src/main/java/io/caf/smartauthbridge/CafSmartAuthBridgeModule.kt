package io.caf.smartauthbridge

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import io.caf.smartauth.input.CafFaceAuthenticatorSettings
import io.caf.smartauth.input.CafSdkPlatform
import io.caf.smartauth.input.CafSmartAuth
import io.caf.smartauth.input.CafVerifyPolicyListener
import io.caf.smartauth.output.CafFailure

class CafSmartAuthBridgeModule : Module() {
    private val context
        get() = requireNotNull(appContext.reactContext)
    private val activity
        get() = requireNotNull(appContext.currentActivity)

    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    override fun definition() = ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('CafSmartAuthBridgeModule')` in JavaScript.
        Name(CAF_SMART_AUTH_MODULE_NAME)

        // Defines event names that the module can send to JavaScript.
        Events(
            CAF_SMART_AUTH_SUCCESS_EVENT,
            CAF_SMART_AUTH_PENDING_EVENT,
            CAF_SMART_AUTH_ERROR_EVENT,
            CAF_SMART_AUTH_CANCEL_EVENT,
            CAF_SMART_AUTH_LOADING_EVENT,
            CAF_SMART_AUTH_LOADED_EVENT
        )

        // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
        Function(CAF_SMART_AUTH_FUNCTION_START_SMART_AUTH) { mfaToken: String, faceAuthToken: String, personId: String, policyId: String, jsonString: String ->
            Handler(Looper.getMainLooper()).post {
                val smartAuth = build(
                    mfaToken = mfaToken,
                    faceAuthToken = faceAuthToken,
                    settings = jsonString
                )

                smartAuth.verifyPolicy(personId, policyId, setupListener())
            }
        }

        // Defines a JavaScript asynchronous function that runs the native code on the JavaScript thread.
        AsyncFunction(CAF_SMART_AUTH_FUNCTION_REQUEST_LOCATION_PERMISSIONS) {
            Handler(Looper.getMainLooper()).post {
                try {
                    requestPermission(
                        permission = Manifest.permission.ACCESS_FINE_LOCATION,
                        activity = activity
                    )
                    requestPermission(
                        permission = Manifest.permission.ACCESS_COARSE_LOCATION,
                        activity = activity
                    )
                } catch (e: Exception) {
                    throw IllegalStateException("$PERMISSION_ERROR_DESCRIPTION ${e.message}")
                }
            }
        }
    }

    private fun build(mfaToken: String, faceAuthToken: String, settings: String): CafSmartAuth {
        val faceAuthenticationSettings = CafSmartAuthBridgeSettings(settings = settings)

        return CafSmartAuth.CafBuilder(mfaToken, context)
            .apply {
                setSdkPlatform(CafSdkPlatform.REACT_NATIVE)
                faceAuthenticationSettings.cafStage?.let { setStage(it) }
                setFaceAuthenticatorSettings(
                    CafFaceAuthenticatorSettings(
                        faceAuthToken,
                        faceAuthenticationSettings.faceAuthenticatorSettings?.loadingScreen,
                        faceAuthenticationSettings.faceAuthenticatorSettings?.enableScreenCapture,
                        faceAuthenticationSettings.faceAuthenticatorSettings?.filter
                    )
                )
            }
            .build()
    }

    private fun setupListener() = object : CafVerifyPolicyListener {
        override fun onSuccess(
            isAuthorized: Boolean,
            attemptId: String?,
            attestation: String?
        ) {
            sendEvent(CAF_SMART_AUTH_SUCCESS_EVENT, mapOf(
                CAF_MAP_KEY_IS_AUTHORIZED to isAuthorized,
                CAF_MAP_KEY_ATTEMPT_ID to (attemptId ?: ""),
                CAF_MAP_KEY_ATTESTATION to (attestation ?: "")
            ))
        }

        override fun onPending(isAuthorized: Boolean, attestation: String?) {
            sendEvent(CAF_SMART_AUTH_PENDING_EVENT, mapOf(
                CAF_MAP_KEY_IS_AUTHORIZED to isAuthorized,
                CAF_MAP_KEY_ATTESTATION to (attestation ?: "")
            ))
        }

        override fun onError(failure: CafFailure) {
            sendEvent(CAF_SMART_AUTH_ERROR_EVENT, mapOf(
                CAF_MAP_KEY_ERROR_MESSAGE to failure.message
            ))
        }

        override fun onCancel() {
            sendEvent(CAF_SMART_AUTH_CANCEL_EVENT, mapOf(CAF_MAP_KEY_IS_CANCELLED to true))
        }

        override fun onLoading() {
            sendEvent(CAF_SMART_AUTH_LOADING_EVENT, mapOf(CAF_MAP_KEY_IS_LOADING to true))
        }

        override fun onLoaded() {
            sendEvent(CAF_SMART_AUTH_LOADED_EVENT, mapOf(CAF_MAP_KEY_IS_LOADED to true))
        }
    }

    private fun requestPermission(permission: String, activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
        }
    }

    companion object {
        private const val REQUEST_CODE = 1234

        private const val CAF_SMART_AUTH_MODULE_NAME = "CafSmartAuthBridgeModule"
        private const val CAF_SMART_AUTH_FUNCTION_START_SMART_AUTH = "startSmartAuth"
        private const val CAF_SMART_AUTH_FUNCTION_REQUEST_LOCATION_PERMISSIONS = "requestLocationPermissions"

        private const val CAF_SMART_AUTH_SUCCESS_EVENT = "CafSmartAuth_Success"
        private const val CAF_SMART_AUTH_PENDING_EVENT = "CafSmartAuth_Pending"
        private const val CAF_SMART_AUTH_ERROR_EVENT = "CafSmartAuth_Error"
        private const val CAF_SMART_AUTH_CANCEL_EVENT = "CafSmartAuth_Cancel"
        private const val CAF_SMART_AUTH_LOADING_EVENT = "CafSmartAuth_Loading"
        private const val CAF_SMART_AUTH_LOADED_EVENT = "CafSmartAuth_Loaded"

        private const val CAF_MAP_KEY_IS_AUTHORIZED = "isAuthorized"
        private const val CAF_MAP_KEY_ATTEMPT_ID = "attemptId"
        private const val CAF_MAP_KEY_ATTESTATION = "attestation"
        private const val CAF_MAP_KEY_ERROR_MESSAGE = "errorMessage"
        private const val CAF_MAP_KEY_IS_CANCELLED = "isCancelled"
        private const val CAF_MAP_KEY_IS_LOADING = "isLoading"
        private const val CAF_MAP_KEY_IS_LOADED = "isLoaded"

        private const val PERMISSION_ERROR_DESCRIPTION = "Error checking location permissions:"
    }
}
