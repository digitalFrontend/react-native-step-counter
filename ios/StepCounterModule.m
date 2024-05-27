#import <React/RCTBridge.h>
#import <React/RCTEventEmitter.h>
#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>


@interface RCT_EXTERN_MODULE(StepCounterModule, RCTEventEmitter)

    RCT_EXTERN_METHOD(isStepCountingSupported:(RCTPromiseResolveBlock)resolve
                      withRejecter: (RCTPromiseRejectBlock)reject)

    RCT_EXTERN_METHOD(queryStepCounterDataBetweenDates:(NSDate *)startDate
                      withEndDate:(NSDate *)endDate
                      withResolver: (RCTPromiseResolveBlock)resolve
                      withRejecter: (RCTPromiseRejectBlock)reject)

    RCT_EXTERN_METHOD(startStepCounterUpdate)

    RCT_EXTERN_METHOD(stopStepCounterUpdate)

    RCT_EXTERN_METHOD(supportedEvents)

    RCT_EXTERN_METHOD(isActive:(RCTPromiseResolveBlock)resolve
                      withRejecter: (RCTPromiseRejectBlock)reject)

@end
