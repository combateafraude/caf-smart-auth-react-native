import Foundation
import React
import CafSmartAuth

private struct CafSmartAuthBridgeConstants { 
    static let cafSmartAuthSuccessEvent: String = "CafSmartAuth_Success"
    static let cafSmartAuthPendingEvent: String = "CafSmartAuth_Pending"
    static let cafSmartAuthErrorEvent: String = "CafSmartAuth_Error"
    static let cafSmartAuthCancelEvent: String = "CafSmartAuth_Cancel"
    static let cafSmartAuthLoadingEvent: String = "CafSmartAuth_Loading"
    static let cafSmartAuthLoadedEvent: String = "CafSmartAuth_Loaded"
    
    static let isAuthorized: String = "isAuthorized"
    static let attestation: String = "attestation"
    static let errorMessage: String = "message"

    static let cafFilterNaturalIndex: Int = 0
  
    static let backgroundColorHex: String = "#FFFFFF"
    static let textColorHex: String = "#FF000000"
    static let primaryColorHex: String = "#004AF7"
    static let boxBackgroundColorHex: String = "#0A004AF7"
}

@objc(CafSmartAuthBridgeModule)
class CafSmartAuthBridgeModule: RCTEventEmitter {
  private var smartAuth: CafSmartAuthSdk?
  
  @objc
  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  override func supportedEvents() -> [String]! {
    return [
      CafSmartAuthBridgeConstants.cafSmartAuthSuccessEvent,
      CafSmartAuthBridgeConstants.cafSmartAuthPendingEvent,
      CafSmartAuthBridgeConstants.cafSmartAuthErrorEvent,
      CafSmartAuthBridgeConstants.cafSmartAuthCancelEvent,
      CafSmartAuthBridgeConstants.cafSmartAuthLoadingEvent,
      CafSmartAuthBridgeConstants.cafSmartAuthLoadedEvent
    ]
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
  
  private func emitEvent(name: String, data: Any) {
    self.sendEvent(withName: name, body: data)
  }
  
  private func setupListener() -> CafVerifyPolicyListener {
    return { result in
      switch result {
      case .onSuccess(let response):
        self.emitEvent(
          name: CafSmartAuthBridgeConstants.cafSmartAuthSuccessEvent,
          data: [
            CafSmartAuthBridgeConstants.isAuthorized: response.isAuthorized,
            CafSmartAuthBridgeConstants.attestation: response.attestation
          ]
        )
        self.smartAuth = nil
        
      case .onPending(let response):
        self.emitEvent(
          name: CafSmartAuthBridgeConstants.cafSmartAuthPendingEvent,
          data: [
            CafSmartAuthBridgeConstants.isAuthorized: response.isAuthorized,
            CafSmartAuthBridgeConstants.attestation: response.attestation
          ]
        )
        
      case .onError(let error):
        self.emitEvent(
          name: CafSmartAuthBridgeConstants.cafSmartAuthErrorEvent,
          data: [CafSmartAuthBridgeConstants.errorMessage: error.error.localizedDescription]
        )
        self.smartAuth = nil
        
      case .onCanceled(_):
        self.emitEvent(
          name: CafSmartAuthBridgeConstants.cafSmartAuthCancelEvent,
          data: true
        )
        self.smartAuth = nil
        
      case .onLoading:
        self.emitEvent(name: CafSmartAuthBridgeConstants.cafSmartAuthLoadingEvent, data: true)
        
      case .onLoaded:
        self.emitEvent(name: CafSmartAuthBridgeConstants.cafSmartAuthLoadedEvent, data: true)
      }
    }
  }
  
  
  @objc(startSmartAuth:livenessToken:personId:policyId:settings:)
  func startSmartAuth(mfaToken: String, faceAuthToken: String, personId: String, policyId: String, settings: String?) {
    DispatchQueue.main.async {
      self.smartAuth = self.build(
        mfaToken: mfaToken, faceAuthToken: faceAuthToken, settings: CafSmartAuthBridgeSettings().parseJson(settings: settings)
      )
      
      self.smartAuth?.verifyPolicy(personID: personId, policyId: policyId, listener: self.setupListener())
    }
  }
}
