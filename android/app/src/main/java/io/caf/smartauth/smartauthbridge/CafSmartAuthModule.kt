package io.caf.smartauth.smartauthbridge

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import io.caf.smartauth.input.CafFaceAuthenticatorSettings
import io.caf.smartauth.input.CafSdkPlatform
import io.caf.smartauth.input.CafSmartAuth
import io.caf.smartauth.input.CafVerifyPolicyListener
import io.caf.smartauth.output.CafFailure
import javax.annotation.Nonnull

class CafSmartAuthModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    @Nonnull
    override fun getName(): String {
        return CAF_SMART_AUTH_MODULE
    }

    private fun build(mfaToken: String, faceAuthToken: String, settings: String): CafSmartAuth {
        val faceAuthenticationSettings = CafSmartAuthSettings(settings = settings)

        return CafSmartAuth.CafBuilder(mfaToken, reactContext)
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

    private fun emitEvent(eventName: String, params: Any) {
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    private fun setupListener() = object : CafVerifyPolicyListener {
        override fun onSuccess(
            isAuthorized: Boolean,
            attemptId: String?,
            attestation: String?
        ) {
            emitEvent(eventName = CAF_SMART_AUTH_SUCCESS_EVENT, params = WritableNativeMap().apply {
                putBoolean(CAF_WRITABLE_MAP_IS_AUTHORIZED, isAuthorized)
                putString(CAF_WRITABLE_MAP_ATTEMPT_ID, attemptId)
                putString(CAF_WRITABLE_MAP_ATTESTATION, attestation)
            })
        }

        override fun onPending(isAuthorized: Boolean, attestation: String) {
            emitEvent(eventName = CAF_SMART_AUTH_PENDING_EVENT, params = WritableNativeMap().apply {
                putBoolean(CAF_WRITABLE_MAP_IS_AUTHORIZED, isAuthorized)
                putString(CAF_WRITABLE_MAP_ATTESTATION, attestation)
            })
        }

        override fun onError(failure: CafFailure) {
            emitEvent(eventName = CAF_SMART_AUTH_ERROR_EVENT, params = WritableNativeMap().apply {
                putString(CAF_WRITABLE_MAP_ERROR_MESSAGE, failure.message)
            })
        }

        override fun onCancel() {
            emitEvent(eventName = CAF_SMART_AUTH_CANCEL_EVENT, params = true)
        }

        override fun onLoading() {
            emitEvent(eventName = CAF_SMART_AUTH_LOADING_EVENT, params = true)
        }

        override fun onLoaded() {
            emitEvent(eventName = CAF_SMART_AUTH_LOADED_EVENT, params = true)
        }
    }

    private fun requestPermission(permission: String, activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                reactContext,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
        }
    }

    @ReactMethod
    fun startSmartAuth(
        mfaToken: String,
        faceAuthToken: String,
        personId: String,
        policyId: String,
        jsonString: String
    ) {
        Handler(Looper.getMainLooper()).post {
            val smartAuth =
                build(mfaToken = mfaToken, faceAuthToken = faceAuthToken, settings = jsonString)
            smartAuth.verifyPolicy(personId, policyId, setupListener())
        }
    }

    @ReactMethod
    fun requestLocationPermissions(promise: Promise) {
        val activity = currentActivity ?: return

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
                promise.resolve(true)
            } catch (e: Exception) {
                promise.reject(
                    "PERMISSION_ERROR",
                    "Error checking location permissions: ${e.message}"
                )
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 1234

        private const val CAF_SMART_AUTH_MODULE = "CafSmartAuthModule"

        private const val CAF_SMART_AUTH_SUCCESS_EVENT = "CafSmartAuth_Success"
        private const val CAF_SMART_AUTH_PENDING_EVENT = "CafSmartAuth_Pending"
        private const val CAF_SMART_AUTH_ERROR_EVENT = "CafSmartAuth_Error"
        private const val CAF_SMART_AUTH_CANCEL_EVENT = "CafSmartAuth_Cancel"
        private const val CAF_SMART_AUTH_LOADING_EVENT = "CafSmartAuth_Loading"
        private const val CAF_SMART_AUTH_LOADED_EVENT = "CafSmartAuth_Loaded"

        private const val CAF_WRITABLE_MAP_IS_AUTHORIZED = "isAuthorized"
        private const val CAF_WRITABLE_MAP_ATTEMPT_ID = "attemptId"
        private const val CAF_WRITABLE_MAP_ATTESTATION = "attestation"

        private const val CAF_WRITABLE_MAP_ERROR_MESSAGE = "message"
    }
}
