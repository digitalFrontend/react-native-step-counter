import CallKit
import CoreMotion
import Foundation



@objc(StepCounterModule)
class StepCounterModule: RCTEventEmitter {
    
    private var pedometr: CMPedometer? = nil;
    private var stepUpdateActive: Bool = false;
    private var currentSteps: NSNumber = 0;
    static var ERROR_CODE = "RNStepCounterError"
    
    override init() {
        super.init()
        EventEmitter.sharedInstance.registerEventEmitter(eventEmitter: self)
        self.pedometr = CMPedometer()
    }
    
    @objc open override func supportedEvents() -> [String] {
        return EventEmitter.sharedInstance.allEvents
    }


    @objc
    func isStepCountingSupported(
        _ resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
            DispatchQueue.main.async {
                let status = CMPedometer.authorizationStatus()
                resolve(["granted": status == CMAuthorizationStatus.authorized,
                         "supported": CMPedometer.isStepCountingAvailable()]);
           }
    }
    
    @objc
    func queryStepCounterDataBetweenDates(
        _ startDate: Date,
        withEndDate endDate: Date,
        withResolver resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) {
            DispatchQueue.main.async {
                self.pedometr?.queryPedometerData(from: startDate, to: endDate, withHandler: { pedometerData, error in
                    if (error != nil){
                        reject(StepCounterModule.ERROR_CODE, error?.localizedDescription, error)
                    } else {
                        resolve(DictionaryEncoder.convertPedometerData(data: pedometerData!));
                    }
                });
           }
    }
    
    @objc
    func openAppSettings() {
        DispatchQueue.main.async {
            UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)
       }
    }
    
    @objc
    func startStepCounterUpdate() {
        DispatchQueue.main.async {
            self.stepUpdateActive = true
            self.pedometr?.startUpdates(from: Date(), withHandler: {pedometerData,errorPed in
                if (errorPed == nil) {
                    if (pedometerData?.numberOfSteps != nil){
                        let diff = NSNumber(value: pedometerData!.numberOfSteps.floatValue - self.currentSteps.floatValue);
                        
                        if (diff.floatValue > 0){
                            self.currentSteps = pedometerData!.numberOfSteps;
                            
                            
                            EventEmitter.sharedInstance.dispatch(name: "onStepsChanged", body: DictionaryEncoder.convertSteps(steps: diff))
                        }
                    }
                    
                } else {
                    EventEmitter.sharedInstance.dispatch(name: "onStepsChangedError", body: DictionaryEncoder.convertError(error: errorPed?.localizedDescription ?? "Unknown error"))
                    
                }
            })
       }
    }
    
    @objc
    func stopStepCounterUpdate() {
        DispatchQueue.main.async {
            self.stepUpdateActive = false
            self.pedometr?.stopUpdates();
            self.currentSteps = 0;
       }
    }
    
    @objc
    func isActive(
        _ resolve: @escaping RCTPromiseResolveBlock,
        withRejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
            DispatchQueue.main.async {
                resolve(self.stepUpdateActive);
           }
    }
}
