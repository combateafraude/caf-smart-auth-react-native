import ExpoModulesCore
import CafSmartAuth

private struct CafSmartAuthBridgeConstants {
    static let moduleName: String = "CafSmartAuthBridgeModule"
    static let startSmartAuth: String = "startSmartAuth"
    
    static let cafSmartAuthSuccessEvent: String = "CafSmartAuth_Success"
    static let cafSmartAuthPendingEvent: String = "CafSmartAuth_Pending"
    static let cafSmartAuthErrorEvent: String = "CafSmartAuth_Error"
    static let cafSmartAuthCancelEvent: String = "CafSmartAuth_Cancel"
    static let cafSmartAuthLoadingEvent: String = "CafSmartAuth_Loading"
    static let cafSmartAuthLoadedEvent: String = "CafSmartAuth_Loaded"
    
    static let isAuthorized: String = "isAuthorized"
    static let attestation: String = "attestation"
    static let errorMessage: String = "message"
    static let isCancelled: String = "isCancelled"
    static let isLoading: String = "isLoading"
    static let isLoaded: String = "isLoaded"

    static let cafFilterNaturalIndex: Int = 0
    
    static let backgroundColorHex: String = "#FFFFFF"
    static let textColorHex: String = "#FF000000"
    static let primaryColorHex: String = "#004AF7"
    static let boxBackgroundColorHex: String = "#0A004AF7"
}

public class CafSmartAuthBridgeModule: Module {
    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    
    private var smartAuth: CafSmartAuthSdk?
    
    public func definition() -> ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('CafSmartAuthBridgeModule')` in JavaScript.
        Name(CafSmartAuthBridgeConstants.moduleName)
        
        // Defines event names that the module can send to JavaScript.
        Events(
            CafSmartAuthBridgeConstants.cafSmartAuthSuccessEvent,
            CafSmartAuthBridgeConstants.cafSmartAuthPendingEvent,
            CafSmartAuthBridgeConstants.cafSmartAuthErrorEvent,
            CafSmartAuthBridgeConstants.cafSmartAuthCancelEvent,
            CafSmartAuthBridgeConstants.cafSmartAuthLoadingEvent,
            CafSmartAuthBridgeConstants.cafSmartAuthLoadedEvent
        )
        
        // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
        Function(CafSmartAuthBridgeConstants.startSmartAuth) { (mfaToken: String, faceAuthToken: String, personId: String, policyId: String, settings: String?) -> Void in
            DispatchQueue.main.async {
               
                self.smartAuth = self.build(
                    mfaToken: mfaToken, faceAuthToken: faceAuthToken, settings: CafSmartAuthBridgeSettings().parseJson(settings: settings)
                )
                
                self.smartAuth?.verifyPolicy(personID: personId, policyId: policyId, listener: self.setupListener())
            }
        }
    }
    
    private func build(
        mfaToken: String,
        faceAuthToken: String,
        settings: CafSmartAuthBridgeSettingsModel?
    ) -> CafSmartAuthSdk {
        let builder = CafSmartAuthSdk.CafBuilder(mobileToken: mfaToken)
        
        if let stage = settings?.stage, let cafStage = CafEnvironment(rawValue: stage) {
            _ = builder.setStage(cafStage)
        }
        
        if let emailUrl = settings?.emailUrl {
          _ = builder.setEmailURL(URL(string: emailUrl))
        }
        
        if let phoneUrl = settings?.phoneUrl {
          _ = builder.setPhoneURL(URL(string: phoneUrl))
        }
        
        let filter: CafFilterStyle = {
            if let faceSettings = settings?.faceAuthenticationSettings, faceSettings.filter == CafSmartAuthBridgeConstants.cafFilterNaturalIndex {
                return .natural
            }
            return .lineDrawing
        }()
        
        _ = builder.setLivenessSettings(
            CafFaceLivenessSettings(
                faceLivenessToken: faceAuthToken,
                useLoadingScreen: settings?.faceAuthenticationSettings?.loadingScreen ?? false,
                filter: filter
            )
        )
        
        let lightTheme = settings?.theme?.lightTheme
        let darkTheme = settings?.theme?.darkTheme
        
        _ = builder.setThemeConfigurator(
          CafThemeConfigurator(
            lightTheme: parseTheme(theme: lightTheme),
            darkTheme: parseTheme(theme: darkTheme)
          )
        )
        
        return builder.build()
    }
    
    private func parseTheme(theme: CafSmartAuthBridgeTheme?) -> CafTheme {
      if theme != nil {
        return CafTheme(
          backgroundColor: theme?.backgroundColor ?? CafSmartAuthBridgeConstants.backgroundColorHex,
          textColor: theme?.textColor ?? CafSmartAuthBridgeConstants.textColorHex,
          linkColor: theme?.linkColor ?? CafSmartAuthBridgeConstants.primaryColorHex,
          boxBorderColor: theme?.boxBorderColor ?? CafSmartAuthBridgeConstants.primaryColorHex,
          boxFilledBorderColor: theme?.boxFilledBorderColor ?? CafSmartAuthBridgeConstants.primaryColorHex,
          boxBackgroundColor: theme?.boxBackgroundColor ?? CafSmartAuthBridgeConstants.boxBackgroundColorHex,
          boxFilledBackgroundColor: theme?.boxFilledBackgroundColor ?? CafSmartAuthBridgeConstants.boxBackgroundColorHex,
          boxTextColor: theme?.boxTextColor ?? CafSmartAuthBridgeConstants.primaryColorHex,
          progressColor:  theme?.progressColor ?? CafSmartAuthBridgeConstants.primaryColorHex
        )
      } else {
        return CafTheme(
          backgroundColor: CafSmartAuthBridgeConstants.backgroundColorHex,
          textColor: CafSmartAuthBridgeConstants.textColorHex,
          linkColor: CafSmartAuthBridgeConstants.primaryColorHex,
          boxBorderColor: CafSmartAuthBridgeConstants.primaryColorHex,
          boxFilledBorderColor: CafSmartAuthBridgeConstants.primaryColorHex,
          boxBackgroundColor: CafSmartAuthBridgeConstants.boxBackgroundColorHex,
          boxFilledBackgroundColor: CafSmartAuthBridgeConstants.boxBackgroundColorHex,
          boxTextColor: CafSmartAuthBridgeConstants.primaryColorHex,
          progressColor: CafSmartAuthBridgeConstants.primaryColorHex
        )
      }
    }
    
    private func setupListener() -> CafVerifyPolicyListener {
        return { result in
            switch result {
            case .onSuccess(let response):
                self.sendEvent(CafSmartAuthBridgeConstants.cafSmartAuthSuccessEvent, [
                    CafSmartAuthBridgeConstants.isAuthorized: response.isAuthorized,
                    CafSmartAuthBridgeConstants.attestation: response.attestation
                ])
                self.smartAuth = nil
                
            case .onPending(let response):
                self.sendEvent(CafSmartAuthBridgeConstants.cafSmartAuthPendingEvent, [
                    CafSmartAuthBridgeConstants.isAuthorized: response.isAuthorized,
                    CafSmartAuthBridgeConstants.attestation: response.attestation
                ])
                
            case .onError(let error):
                self.sendEvent(CafSmartAuthBridgeConstants.cafSmartAuthErrorEvent, [
                    CafSmartAuthBridgeConstants.errorMessage: error.localizedDescription
                ])
                self.smartAuth = nil
                
            case .onCanceled(_):
                self.sendEvent(CafSmartAuthBridgeConstants.cafSmartAuthCancelEvent, [
                    CafSmartAuthBridgeConstants.isCancelled: true
                ])
                self.smartAuth = nil
                
            case .onLoading:
                self.sendEvent(CafSmartAuthBridgeConstants.cafSmartAuthLoadingEvent, [
                    CafSmartAuthBridgeConstants.isLoading: true
                ])
                
            case .onLoaded:
                self.sendEvent(CafSmartAuthBridgeConstants.cafSmartAuthLoadedEvent, [
                    CafSmartAuthBridgeConstants.isLoaded: true
                ])
            }
        }
    }
}
