#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(CafSmartAuthModule, RCTEventEmitter)
RCT_EXTERN_METHOD(startSmartAuth:(NSString *)mfaToken livenessToken:(NSString *)livenessToken personId:(NSString *)personId policyId:(NSString *)policyId settings:(NSString *)settings)
@end
