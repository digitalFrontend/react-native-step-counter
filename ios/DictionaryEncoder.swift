
import CoreMotion

struct DictionaryEncoder {
    static func encode<T>(_ value: T) throws -> [String: Any] where T: Encodable {
        let jsonData = try JSONEncoder().encode(value)
        return try JSONSerialization.jsonObject(with: jsonData) as? [String: Any] ?? [:]
    }
    
    static func convertPedometerData(data: CMPedometerData) -> [String:Any?] {
        
        var map = [String:Any?]()
        
        map.updateValue("CMPedometer", forKey: "counterType")
        map.updateValue(data.numberOfSteps, forKey: "steps")
        map.updateValue(data.distance, forKey: "distance")
        map.updateValue(data.floorsAscended, forKey: "floorsAscended")
        map.updateValue(data.floorsDescended, forKey: "floorsDescended")
        
        return map;
    }
    
    static func convertSteps(steps: NSNumber) -> [String:Any?] {
        
        var map = [String:Any?]()
        
        map.updateValue(steps, forKey: "steps")
        
        return map;
    }
    
    static func convertError(error: String) -> [String:Any?] {
        
        var map = [String:Any?]()
        
        map.updateValue(error, forKey: "error")
        
        return map;
    }
}
