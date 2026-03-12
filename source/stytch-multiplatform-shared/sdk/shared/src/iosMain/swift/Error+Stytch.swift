import Foundation

public extension Error {
    var asStytchError: StytchError? {
        (self as NSError).userInfo["KotlinException"] as? StytchError
    }
}
