import Foundation
import CafSmartAuth

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
