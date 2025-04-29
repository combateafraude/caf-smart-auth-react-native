package io.caf.smartauthbridge

import io.caf.smartauth.domain.model.CafTheme
import io.caf.smartauth.input.CafFilterStyle
import io.caf.smartauth.input.CafStage
import org.json.JSONObject
import java.io.Serializable

internal data class CafFaceAuthenticationSettingsModel(
    val loadingScreen: Boolean?,
    val enableScreenCapture: Boolean?,
    val filter: CafFilterStyle?
)

internal data class CafSmartAuthBridgeTheme(
    val lightTheme: CafTheme,
    val darkTheme: CafTheme
)

internal class CafSmartAuthBridgeSettings(settings: String) : Serializable {
    val cafStage: CafStage?
    val emailUrl: String?
    val phoneUrl: String?
    val faceAuthenticatorSettings: CafFaceAuthenticationSettingsModel?
    val theme: CafSmartAuthBridgeTheme?

    init {
        val jsonObject = JSONObject(settings)

        cafStage = jsonObject.takeIf {
            it.has(STAGE)
        }?.let {
            CafStage.values()[it.getInt(STAGE)]
        }

        emailUrl = jsonObject.takeIf {
            it.has(EMAIL)
        }?.getString(EMAIL)

        phoneUrl = jsonObject.takeIf {
            it.has(PHONE)
        }?.getString(PHONE)

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
                faceAuthenticatorSettings.getBoolean(LOADING_SCREEN),
                faceAuthenticatorSettings.optBoolean(ENABLE_SCREEN_CAPTURE),
                filterStyle ?: CafFilterStyle.LINE_DRAWING
            )
        }

        theme = jsonObject.takeIf {
            it.has(THEME)
        }?.let {
            val theme = it.getJSONObject(THEME)

            val lightTheme = theme.optJSONObject(LIGHT_THEME)
            val darkTheme = theme.optJSONObject(DARK_THEME)

            CafSmartAuthBridgeTheme(
                lightTheme = parseTheme(lightTheme),
                darkTheme = parseTheme(darkTheme)
            )
        }
    }

    private fun parseTheme(json: JSONObject?): CafTheme {
        return json?.let {
            CafTheme(
                backgroundColor = json.optString(BACKGROUND_COLOR).takeIf { it.isNotEmpty() }
                    ?: BACKGROUND_COLOR_HEX,
                textColor = json.optString(TEXT_COLOR).takeIf { it.isNotEmpty() }
                    ?: TEXT_COLOR_HEX,
                progressColor = json.optString(PROGRESS_COLOR).takeIf { it.isNotEmpty() }
                    ?: PRIMARY_COLOR_HEX,
                linkColor = json.optString(LINK_COLOR).takeIf { it.isNotEmpty() }
                    ?: PRIMARY_COLOR_HEX,
                boxBackgroundColor = json.optString(BOX_BACKGROUND_COLOR).takeIf { it.isNotEmpty() }
                    ?: BOX_BACKGROUND_COLOR_HEX,
                boxBorderColor = json.optString(BOX_BORDER_COLOR).takeIf { it.isNotEmpty() }
                    ?: PRIMARY_COLOR_HEX,
                boxTextColor = json.optString(BOX_TEXT_COLOR).takeIf { it.isNotEmpty() }
                    ?: PRIMARY_COLOR_HEX
            )
        } ?: CafTheme(
            backgroundColor = BACKGROUND_COLOR_HEX,
            textColor = TEXT_COLOR_HEX,
            progressColor = PRIMARY_COLOR_HEX,
            linkColor = PRIMARY_COLOR_HEX,
            boxBackgroundColor = BOX_BACKGROUND_COLOR_HEX,
            boxBorderColor = PRIMARY_COLOR_HEX,
            boxTextColor = PRIMARY_COLOR_HEX
        )
    }

    private companion object {
        const val STAGE = "stage"
        const val EMAIL = "emailUrl"
        const val PHONE = "phoneUrl"
        const val FACE_AUTHENTICATION_SETTINGS = "faceAuthenticationSettings"
        const val LOADING_SCREEN = "loadingScreen"
        const val ENABLE_SCREEN_CAPTURE = "enableScreenCapture"
        const val FILTER = "filter"
        const val THEME = "theme"
        const val LIGHT_THEME = "lightTheme"
        const val DARK_THEME = "darkTheme"
        const val BACKGROUND_COLOR = "backgroundColor"
        const val TEXT_COLOR = "textColor"
        const val PROGRESS_COLOR = "progressColor"
        const val LINK_COLOR = "linkColor"
        const val BOX_BACKGROUND_COLOR = "boxBackgroundColor"
        const val BOX_BORDER_COLOR = "boxBorderColor"
        const val BOX_TEXT_COLOR = "boxTextColor"

        const val BACKGROUND_COLOR_HEX = "#FFFFFF"
        const val TEXT_COLOR_HEX = "#FF000000"
        const val PRIMARY_COLOR_HEX = "#004AF7"
        const val BOX_BACKGROUND_COLOR_HEX = "#0A004AF7"
    }
}
