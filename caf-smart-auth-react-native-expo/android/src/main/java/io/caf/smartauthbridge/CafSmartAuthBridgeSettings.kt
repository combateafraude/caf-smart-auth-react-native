package io.caf.smartauthbridge

import io.caf.smartauth.input.CafFilterStyle
import io.caf.smartauth.input.CafStage
import org.json.JSONObject
import java.io.Serializable

internal data class CafFaceAuthenticationSettingsModel(
    val loadingScreen: Boolean?,
    val enableScreenCapture: Boolean?,
    val filter: CafFilterStyle?
)

internal class CafSmartAuthBridgeSettings(settings: String) : Serializable {
    val cafStage: CafStage?
    val faceAuthenticatorSettings: CafFaceAuthenticationSettingsModel?

    init {
        val jsonObject = JSONObject(settings)

        cafStage = if (jsonObject.has(STAGE)) CafStage.entries[jsonObject.getInt(STAGE)]
        else null

        faceAuthenticatorSettings = if (jsonObject.has(FACE_AUTHENTICATION_SETTINGS)) {
            val faceAuthenticatorSettings = jsonObject.getJSONObject(FACE_AUTHENTICATION_SETTINGS)

            val filterStyle = if (faceAuthenticatorSettings.has(FILTER)) {
                CafFilterStyle.entries[faceAuthenticatorSettings.getInt(FILTER)]
            } else null

            CafFaceAuthenticationSettingsModel(
                faceAuthenticatorSettings.optBoolean(LOADING_SCREEN),
                faceAuthenticatorSettings.optBoolean(ENABLE_SCREEN_CAPTURE),
                filterStyle ?: CafFilterStyle.LINE_DRAWING
            )
        } else null
    }

    private companion object {
        const val STAGE = "stage"
        const val FACE_AUTHENTICATION_SETTINGS = "faceAuthenticationSettings"
        const val LOADING_SCREEN = "loadingScreen"
        const val ENABLE_SCREEN_CAPTURE = "enableScreenCapture"
        const val FILTER = "filter"
    }
}