package com.cafbridge_identity;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class CafIdentity extends ReactContextBaseJavaModule {
    private Intent intent;

    @NonNull
    @Override
    public String getName() {
        return "CafIdentity";
    }

    public CafIdentity(ReactApplicationContext reactContext) {
        super(reactContext);
        intent = new Intent(getReactApplicationContext(), CafIdentityActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @ReactMethod
    public void identity(String token, String personId, String policyId, String config) {
        intent.putExtra("token", token);
        intent.putExtra("personId", personId);
        intent.putExtra("policyId", policyId);
        intent.putExtra("config", config);
        getReactApplicationContext().startActivity(intent);
    }
}
