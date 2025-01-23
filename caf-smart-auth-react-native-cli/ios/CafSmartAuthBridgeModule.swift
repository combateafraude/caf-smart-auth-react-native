import Foundation
import React
import CafSmartAuth

private struct CafSmartAuthBridgeEvents {
  static let cafSmartAuthSuccessEvent: String = "CafSmartAuth_Success"
  static let cafSmartAuthPendingEvent: String = "CafSmartAuth_Pending"
  static let cafSmartAuthErrorEvent: String = "CafSmartAuth_Error"
  static let cafSmartAuthCancelEvent: String = "CafSmartAuth_Cancel"
  static let cafSmartAuthLoadingEvent: String = "CafSmartAuth_Loading"
  static let cafSmartAuthLoadedEvent: String = "CafSmartAuth_Loaded"
}

internal struct CafFaceAuthenticationSettingsModel: Decodable {
  let loadingScreen: Bool?
  let filter: Int?
}

internal struct CafSmartAuthBridgeSettingsModel: Decodable {
  let stage: Int?
  let faceAuthenticationSettings: CafFaceAuthenticationSettingsModel?
}

private struct CafMutableDictionaries {
  static let isAuthorized: String = "isAuthorized"
  static let attestation: String = "attestation"
  static let message: String = "message"
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
      CafSmartAuthBridgeEvents.cafSmartAuthSuccessEvent,
      CafSmartAuthBridgeEvents.cafSmartAuthPendingEvent,
      CafSmartAuthBridgeEvents.cafSmartAuthErrorEvent,
      CafSmartAuthBridgeEvents.cafSmartAuthCancelEvent,
      CafSmartAuthBridgeEvents.cafSmartAuthLoadingEvent,
      CafSmartAuthBridgeEvents.cafSmartAuthLoadedEvent
    ]
  }
  
  private func build(
    mfaToken: String,
    faceAuthToken: String,
    settings: CafSmartAuthBridgeSettingsModel?
  ) -> CafSmartAuthSdk {
    let builder = CafSmartAuthSdk.CafBuilder(mobileToken: mfaToken)
    
    if let stage = settings?.stage, let cafStage = CAFStage(rawValue: stage) {
      _ = builder.setStage(cafStage)
    }
    
    let filter: CafFilterStyle = {
      if let faceSettings = settings?.faceAuthenticationSettings, faceSettings.filter == 0 {
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
    
    return builder.build()
  }
  
  private func emitEvent(name: String, data: Any) {
    self.sendEvent(withName: name, body: data)
  }
  
  private func setupListener() -> CafVerifyPolicyListener {
    return { result in
      switch result {
      case .onSuccess(let response):
        self.emitEvent(
          name: CafSmartAuthBridgeEvents.cafSmartAuthSuccessEvent,
          data: [
            CafMutableDictionaries.isAuthorized: response.isAuthorized,
            CafMutableDictionaries.attestation: response.attestation
          ]
        )
        self.smartAuth = nil
        
      case .onPending(let response):
        self.emitEvent(
          name: CafSmartAuthBridgeEvents.cafSmartAuthPendingEvent,
          data: [
            CafMutableDictionaries.isAuthorized: response.isAuthorized,
            CafMutableDictionaries.attestation: response.attestation
          ]
        )
        
      case .onError(let error):
        self.emitEvent(
          name: CafSmartAuthBridgeEvents.cafSmartAuthErrorEvent,
          data: [CafMutableDictionaries.message: error.localizedDescription]
        )
        self.smartAuth = nil
        
      case .onCanceled(_):
        self.emitEvent(
          name: CafSmartAuthBridgeEvents.cafSmartAuthCancelEvent,
          data: true
        )
        self.smartAuth = nil
        
      case .onLoading:
        self.emitEvent(name: CafSmartAuthBridgeEvents.cafSmartAuthLoadingEvent, data: true)
        
      case .onLoaded:
        self.emitEvent(name: CafSmartAuthBridgeEvents.cafSmartAuthLoadedEvent, data: true)
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
