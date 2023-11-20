//
//  CafIdentity.m
//  cafbridge_identity
//
//  Created by Lorena Zanferrari on 19/11/23.
//

#import <Foundation/Foundation.h>

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(CafIdentity, RCTEventEmitter)
  RCT_EXTERN_METHOD(identity:(NSString *)token personId:(NSString *)personId policyId:(NSString *)policyId config:(NSString *)config)
@end
