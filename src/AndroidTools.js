import { Platform } from 'react-native'
import NativeModuleExports from './NativeModuleExports'

const AndroidTools = {
    startBackgroundService: async () => {
        if (Platform.OS == 'android') {
            return await NativeModuleExports.startBackgroundService()
        } else {
            return null
        }
    },
    stopBackgroundService: () => {
        if (Platform.OS == 'android') {
            NativeModuleExports.stopBackgroundService()
        }
    },
    isActiveBackgroundService: async () => {
        if (Platform.OS == 'android') {
            return await NativeModuleExports.isActiveBackgroundService()
        } else {
            return true
        }
    },
    dropDBs: () => {
        NativeModuleExports.dropDBs()
    },
    getAllDetailedRecords: async () => {
        return await NativeModuleExports.getAllDetailedRecords()
    },
    getAllRecords: async () => {
        return await NativeModuleExports.getAllRecords()
    }
}

export default AndroidTools
