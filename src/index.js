import { Platform } from 'react-native'
import NativeModuleExports from './NativeModuleExports'
import AndroidTools from './AndroidTools'

let callbacks = []
let inited = false
let unsub = null

const onEvent = (data) => {
    callbacks.forEach((callback) => {
        try {
            callback(data)
        } catch (err) {
            console.log('StepCounter', err)
        }
    })
}

const init = () => {
    if (inited) {
        return
    }

    unsub = NativeModuleExports.subscribe(onEvent)
    NativeModuleExports.startStepCounterUpdate()
    inited = true
}

const deinit = () => {
    if (!inited) {
        return
    }

    if (callbacks.length == 0) {
        if (unsub) {
            unsub()
            unsub = null
        }
        NativeModuleExports.stopStepCounterUpdate()
        inited = false
    }
}

const StepCounter = {
    isSupported: async () => {
        return await NativeModuleExports.isStepCountingSupported()
    },
    askPermissions: async () => {
        if (Platform.OS == 'android') {
            return await NativeModuleExports.askPermissions()
        } else {
            let date1 = new Date()
            date1.setMinutes(0)
            let date2 = new Date()
            date2.setMinutes(1)
            let queryResult = await NativeModuleExports.queryStepCounterDataBetweenDates(date1.getTime(), date2.getTime())
            return queryResult?.steps != null
        }
    },
    query: async (date1, date2) => {
        if (date1 > date2) {
            return await NativeModuleExports.queryStepCounterDataBetweenDates(date2, date1)
        } else {
            return await NativeModuleExports.queryStepCounterDataBetweenDates(date1, date2)
        }
    },
    isActive: async () => {
        return await NativeModuleExports.isActive()
    },
    isActiveBackgroundService: async () => {
        return AndroidTools.isActiveBackgroundService()
    },
    onStepUpdates: (callback) => {
        callbacks.push(callback)
        init()

        return () => {
            callbacks = callbacks.filter((c) => c != callback)
            deinit()
        }
    },
    android: AndroidTools
}

export default StepCounter
