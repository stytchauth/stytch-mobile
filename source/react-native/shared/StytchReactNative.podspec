require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

# TODO: Figure out how to make this agnostic/not me
# TODO: Update publish workflow to replace this with the live repo URL
spmUrl = File.dirname('/Users/jhaven/Documents/stytch-mobile/source/ios/Package.swift')

Pod::Spec.new do |s|
  s.name         = "StytchReactNative"
  s.version      = package["version"]
  s.summary      = package['summary']
  s.description  = package['description']
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "13.0" }
  s.source       = { :git => "https://github.com/stytchauth/stytch-mobile.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift,cpp}"
  s.private_header_files = "ios/**/*.h"
  s.public_header_files = "ios/**/*.h"

  spm_dependency(s,
    url: spmUrl,
    requirement: {},
    products: ['StytchConsumerSDK'] # We're only using the shared module from this, so it doesn't matter if it's the consumer or b2b one :)
  )

  s.pod_target_xcconfig = {
    'DEFINES_MODULE' => 'YES',
    'SWIFT_COMPILATION_MODE' => 'wholemodule'
  }

  install_modules_dependencies(s)
end
