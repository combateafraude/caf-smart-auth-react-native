//
//  CafIdentity.swift
//  cafbridge_identity
//
//  Created by Lorena Zanferrari on 19/11/23.
//

import Foundation
import React
import Identity
import FaceLiveness

@objc(CafIdentity)
class CAFIdentity: RCTEventEmitter {
  
  @objc
  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  override func supportedEvents() -> [String]! {
    return [
      "FaceLiveness_Success",
      "FaceLiveness_Error",
      "FaceLiveness_Cancel",
      "FaceLiveness_Loading",
      "FaceLiveness_Loaded",
      "FaceAuthenticator_Success",
      "FaceAuthenticator_Error",
      "FaceAuthenticator_Cancel",
      "FaceAuthenticator_Loading",
      "FaceAuthenticator_Loaded",
      "Identity_Success",
      "Identity_Pending",
      "Identity_Error",
      "Identity_Canceled"
    ]
  }
  
  @objc(identity:personId:policyId:config:)
    func identity(token: String, personId: String, policyId: String, config: String) {
        var configDictionary: [String: Any]? = nil
        if let data = config.data(using: .utf8) {
            configDictionary = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
        }
        
        var emailURL: URL? = nil
        var phoneURL: URL? = nil
        var cafStage: CAFStage = .PROD
        var livenessToken: String? = nil
      
        if let email = configDictionary?["emailURL"] as? String {
            emailURL = URL(string: email)
        }
        
        if let phone = configDictionary?["phoneURL"] as? String {
            phoneURL = URL(string: phone)
        }
      
        if let newLivenessToken = configDictionary?["livenessToken"] as? String {
          livenessToken = newLivenessToken
        }
      
      
        if let cafStageValue = configDictionary?["cafStage"] as? Int, let newCafStage = CAFStage(rawValue: cafStageValue) {
          cafStage = newCafStage
        }
        
        
      let identity = IdentitySDK.Builder(mobileToken: token, livenessToken: livenessToken!)
          .setStage(cafStage)
          .setEmailURL(emailURL)
          .setPhoneURL(phoneURL)
          .build()
        
      DispatchQueue.main.async {
        identity.verifyPolicy(personID: personId, policyId: policyId) { verifyPolicyResult in
          switch verifyPolicyResult {
            
          case .onSuccess((let isAuthorized, let attestation)):
            let response : NSMutableDictionary = [:]
            response["authorized"] = isAuthorized
            response["attestation"] = attestation
            self.sendEvent(withName: "Identity_Success", body: response)
            break
          case .onPending((let isAuthorized, let attestation)):
            let response : NSMutableDictionary = [:]
            response["pending"] = isAuthorized
            response["attestation"] = attestation
            self.sendEvent(withName: "Identity_Pending", body: response)
            break
          case .onError(let error):
            let response : NSMutableDictionary = [:]
            response["message"] = error.errorDescription
            response["type"] = "Error"
            self.sendEvent(withName: "Identity_Error", body: response)
            break
          case .onCanceled(_):
            let response : NSMutableDictionary = [:]
            response["message"] = "User canceled the action"
            response["type"] = "Canceled"
            self.sendEvent(withName: "Identity_Canceled", body: response)
            break
          }
        }
      }

    }
}

