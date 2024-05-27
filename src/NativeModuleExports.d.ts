declare const NativeModuleExports: {
    isStepCountingSupported: () => Promise<any>
    askPermissions: () => Promise<any>
    queryStepCounterDataBetweenDates: (startDate: number, endDate: number) => Promise<any>
    isActive: () => Promise<any>
    startStepCounterUpdate: () => void
    stopStepCounterUpdate: () => void
    startBackgroundService: () => Promise<any>
    stopBackgroundService: () => void
    isActiveBackgroundService: () => Promise<any>
    dropDBs: () => void
    getAllDetailedRecords: () => Promise<any>
    getAllRecords: () => Promise<any>
    openAppSettings: () => void
    subscribe: (callback: Function) => () => void
}
export default NativeModuleExports
