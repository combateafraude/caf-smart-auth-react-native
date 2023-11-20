package com.cafbridge_identity;

import com.combateafraude.identity.input.CafStage;
import com.combateafraude.identity.input.FilterStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class IdentityConfig implements Serializable {
    public CafStage cafStage;
    public FilterStyle filter;
    public String  livenessToken;
    public String setEmailUrl;
    public String setPhoneUrl;
    public boolean setEnableScreenshots;
    public boolean setLoadingScreen;


    public IdentityConfig(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        this.cafStage = CafStage.valueOf(jsonObject.getString("cafStage"));
        this.livenessToken = jsonObject.isNull("livenessToken") ? null : jsonObject.getString("livenessToken");
        this.setEmailUrl = jsonObject.isNull("setEmailUrl") ? null : jsonObject.getString("setEmailUrl");
        this.setPhoneUrl = jsonObject.isNull("setPhoneUrl") ? null : jsonObject.getString("setPhoneUrl");
        this.setEnableScreenshots = jsonObject.getBoolean("setEnableScreenshots");
        this.setLoadingScreen = jsonObject.getBoolean("setLoadingScreen");
        this.filter = FilterStyle.valueOf(jsonObject.getString("filter"));
    }
}