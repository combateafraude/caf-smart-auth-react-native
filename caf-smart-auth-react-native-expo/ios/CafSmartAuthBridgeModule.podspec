require 'json'

package = JSON.parse(File.read(File.join(__dir__, '..', 'package.json')))

Pod::Spec.new do |s|
  s.name           = 'CafSmartAuthBridgeModule'
  s.version        = package['version']
  s.summary        = 'Summary'
  s.description    = 'Description'
  s.license        = 'MIT'
  s.author         = 'Eu'
  s.homepage       = 'https://docs.caf.io/sdks'
  s.platforms      = {
    :ios => '15.1',
    :tvos => '15.1'
  }
  s.swift_version  = '5.4'
  s.source         = { git: 'https://github.com/henriquelomarques/caf-react-native-smart-auth-expo' }
  s.static_framework = true

  s.dependency 'ExpoModulesCore'
  s.dependency 'CafSmartAuth', '1.0.0-beta2'

  # Swift/Objective-C compatibility
  s.pod_target_xcconfig = {
    'DEFINES_MODULE' => 'YES',
  }

  s.source_files = "**/*.{h,m,mm,swift,hpp,cpp}"
end
