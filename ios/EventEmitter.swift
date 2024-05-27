class EventEmitter {

    /// Shared Instance.
    public static var sharedInstance = EventEmitter()

    // RnWebim is instantiated by React Native with the bridge.
    private static var eventEmitter: StepCounterModule!

    private init() {}

    // When React Native instantiates the emitter it is registered here.
    func registerEventEmitter(eventEmitter: StepCounterModule) {
        EventEmitter.eventEmitter = eventEmitter
    }

    func dispatch(name: String, body: Any?) {
        EventEmitter.eventEmitter.sendEvent(withName: name, body: body)
    }

    /// All Events which must be support by React Native.
    lazy var allEvents: [String] = {
        var allEventNames: [String] = []
        
        allEventNames.append("onStepsChanged");
        allEventNames.append("onStepsChangedError");
        
        return allEventNames
    }()

}
