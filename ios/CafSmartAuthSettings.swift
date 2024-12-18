import Foundation
import CafSmartAuth

internal class CafSmartAuthSettings {
  internal func parseJson(settings: String) -> CafSmartAuthSettingsModel? {
    guard let data = settings.data(using: .utf8) else {
      return nil
    }
    
    do {
      let decoder = JSONDecoder()
      let parsedSettings = try decoder.decode(CafSmartAuthSettingsModel.self, from: data)
      
      return parsedSettings
    } catch {
      return nil
    }
  }
}
