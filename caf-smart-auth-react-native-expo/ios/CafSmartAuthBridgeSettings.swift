import Foundation
import CafSmartAuth

internal struct CafFaceAuthenticationSettingsModel: Decodable {
    let loadingScreen: Bool?
    let filter: Int?
}

internal struct CafSmartAuthBridgeTheme: Decodable {
  let backgroundColor: String?
  let textColor: String?
  let progressColor: String?
  let linkColor: String?
  let boxBackgroundColor: String?
  let boxFilledBackgroundColor: String?
  let boxBorderColor: String?
  let boxFilledBorderColor: String?
  let boxTextColor: String?
}

internal struct CafSmartAuthBridgeThemeConfigurator: Decodable {
  let lightTheme: CafSmartAuthBridgeTheme?
  let darkTheme: CafSmartAuthBridgeTheme?
}

internal struct CafSmartAuthBridgeSettingsModel: Decodable {
  let stage: Int?
  let faceAuthenticationSettings: CafFaceAuthenticationSettingsModel?
  let emailUrl: String?
  let phoneUrl: String?
  let theme: CafSmartAuthBridgeThemeConfigurator?
}

internal class CafSmartAuthBridgeSettings {
  internal func parseJson(settings: String?) -> CafSmartAuthBridgeSettingsModel? {
    guard let data = settings?.data(using: .utf8) else {
      return nil
    }
    
    do {
      let decoder = JSONDecoder()
      let parsedSettings = try decoder.decode(CafSmartAuthBridgeSettingsModel.self, from: data)
      
      return parsedSettings
    } catch {
      return nil
    }
  }
}
