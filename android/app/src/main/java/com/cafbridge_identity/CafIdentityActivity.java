package com.cafbridge_identity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.combateafraude.identity.input.FaceAuthenticatorSettings;
import com.combateafraude.identity.input.Identity;
import com.combateafraude.identity.input.VerifyPolicyListener;
import com.combateafraude.identity.output.Failure;
import com.combateafraude.identity.output.PolicyReason;
import com.combateafraude.identity.output.SecurityReason;
import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONException;

import kotlin.Result;

public class CafIdentityActivity extends ReactActivity {
    private String token;
    private String personId;
    private String policyId;
    private String customConfig;
    private Intent intent;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        token = intent.getStringExtra("token");
        personId = intent.getStringExtra("personId");
        policyId = intent.getStringExtra("policyId");
        customConfig = intent.getStringExtra("config");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        try {
            this.identity();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void identity() throws JSONException {
        try {
            IdentityConfig config = new IdentityConfig(customConfig);

            Identity identity;

            if (config.livenessToken == null) {
                identity = new Identity.Builder(token, this)
                        .setStage(config.cafStage)
                        .setPhoneUrl(config.setPhoneUrl)
                        .setEmailUrl(config.setEmailUrl)
                        .build();
            } else {
                identity = new Identity.Builder(token, this)
                        .setStage(config.cafStage)
                        .setPhoneUrl(config.setPhoneUrl)
                        .setEmailUrl(config.setEmailUrl)
                        .setFaceAuthenticatorSettings(new FaceAuthenticatorSettings(config.livenessToken, config.setLoadingScreen, config.setEnableScreenshots, config.filter))
                        .build();
            }

            identity.verifyPolicy(personId, policyId, new VerifyPolicyListener() {
                @Override
                public void onSuccess(boolean isAuthorized, @Nullable String attemptId, @Nullable String attestation) {
                    WritableMap writableMap = new WritableNativeMap();
                    writableMap.putBoolean("authorized", isAuthorized);
                    writableMap.putString("attemptId", attemptId);
                    writableMap.putString("attestation", attestation);
                    getReactInstanceManager().getCurrentReactContext()
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("Identity_Success", writableMap);
                    finish();
                }

                @Override
                public void onPending(boolean isPending, String attestation) {
                    WritableMap writableMap = new WritableNativeMap();
                    writableMap.putBoolean("pending", isPending);
                    writableMap.putString("attestation", attestation);
                    getReactInstanceManager().getCurrentReactContext()
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("Identity_Pending", writableMap);
                    finish();
                }

                @Override
                public void onError(Failure failure) {
                    WritableMap writableMap = new WritableNativeMap();
                    String message = "Error: " + failure.getMessage();
                    String type = "Error";
                    if (failure instanceof com.combateafraude.identity.output.NetworkReason){
                        // internet connection failure
                        message = "NetworkError: " + ((com.combateafraude.identity.output.NetworkReason) failure).getThrowable();
                        type = "Network Error";
                    } else if (failure instanceof com.combateafraude.identity.output.ServerReason){
                        // there was a problem in any communication with the CAF servers, let us know!
                        message = "Server Error Code: " + ((com.combateafraude.identity.output.ServerReason) failure).getCode();
                        type = "ServerError";
                    } else if (failure instanceof SecurityReason) {
                        // some security reason on the user's device prevents the use of the SDK
                        message =  "SecurityReason: " + failure.getMessage();
                        type = "SecurityReason";
                    } else if(failure instanceof PolicyReason){
                        // you are using a policy that we do not yet support
                        message =  "PolicyReason: " + failure.getMessage();
                        type = "Policy Reason";
                    } else if(failure.getMessage() == "Cancelled") {
                        message = "User Canceled";
                        type = "Canceled";
                    }
                    writableMap.putString("error", message);
                    writableMap.putString("type", type);
                    getReactInstanceManager().getCurrentReactContext()
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit(type == "Canceled" ? "Identity_Canceled" : "Identity_Error", writableMap);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            WritableMap writableMap = new WritableNativeMap();
            writableMap.putString("error", e.getMessage());
            writableMap.putString("type", "error");
            getReactInstanceManager().getCurrentReactContext()
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("Identity_Error", writableMap);
            finish();
        }
    }

}
