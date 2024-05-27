import { NativeModules, Platform, NativeEventEmitter } from 'react-native'

const { StepCounterModule: Module } = NativeModules
const eventEmitter = new NativeEventEmitter(Module)

const NativeModuleExports = {
    isStepCountingSupported: async () => {
        return await Module.isStepCountingSupported()
    },
    askPermissions: async () => {
        return await Module.askPermissions()
    },
    queryStepCounterDataBetweenDates: async (startDate, endDate) => {
        return await Module.queryStepCounterDataBetweenDates(startDate, endDate)
    },
    isActive: async () => {
        return await Module.isActive()
    },
    startStepCounterUpdate: () => {
        Module.startStepCounterUpdate()
    },
    stopStepCounterUpdate: () => {
        Module.stopStepCounterUpdate()
    },
    startBackgroundService: async () => {
        return await Module.startBackgroundService()
    },
    stopBackgroundService: () => {
        Module.stopBackgroundService()
    },
    isActiveBackgroundService: async () => {
        return await Module.isActiveBackgroundService()
    },
    dropDBs: () => {
        Module.dropDBs()
    },
    getAllDetailedRecords: async () => {
        return await Module.getAllDetailedRecords()
    },
    getAllRecords: async () => {
        return await Module.getAllRecords()
    },
    openAppSettings: () => {
        Module.openAppSettings()
    },
    subscribe: (callback) => {
        let unsubs = []
        let unsub = eventEmitter.addListener('onStepsChanged', (data) => {
            callback({ data: data })
        })

        unsubs.push(unsub)

        unsub = eventEmitter.addListener('onStepsChangedError', (data) => {
            callback({ error: data })
        })
        unsubs.push(unsub)

        return () => {
            unsubs.forEach((u) => {
                u.remove()
            })
        }
    }
}

export default NativeModuleExports
