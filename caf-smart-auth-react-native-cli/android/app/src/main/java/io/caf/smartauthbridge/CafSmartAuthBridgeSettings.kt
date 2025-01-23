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

        cafStage = jsonObject.takeIf {
            it.has(STAGE)
        }?.let {
            CafStage.values()[it.getInt(STAGE)]
        }

        faceAuthenticatorSettings = jsonObject.takeIf {
            it.has(FACE_AUTHENTICATION_SETTINGS)
        }?.let {
            val faceAuthenticatorSettings = it.getJSONObject(FACE_AUTHENTICATION_SETTINGS)

            val filterStyle = faceAuthenticatorSettings.takeIf { settings ->
                settings.has(FILTER)
            }?.let {
                CafFilterStyle.values()[faceAuthenticatorSettings.getInt(FILTER)]
            }

            CafFaceAuthenticationSettingsModel(
                faceAuthenticatorSettings.optBoolean(LOADING_SCREEN),
                faceAuthenticatorSettings.optBoolean(ENABLE_SCREEN_CAPTURE),
                filterStyle ?: CafFilterStyle.LINE_DRAWING
            )
        }
    }

    private companion object {
        const val STAGE = "stage"
        const val FACE_AUTHENTICATION_SETTINGS = "faceAuthenticationSettings"
        const val LOADING_SCREEN = "loadingScreen"
        const val ENABLE_SCREEN_CAPTURE = "enableScreenCapture"
        const val FILTER = "filter"
    }
}
