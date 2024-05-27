declare const StepCounter: {
    isSupported: () => Promise<any>
    askPermissions: () => Promise<any>
    query: (date1: number, date2: number) => Promise<any>
    isActive: () => Promise<any>
    isActiveBackgroundService: () => Promise<any>
    onStepUpdates: (callback: Function) => () => void
    openAppSettings: () => void
    android: {
        startBackgroundService: () => Promise<any>
        stopBackgroundService: () => void
        isActiveBackgroundService: () => Promise<any>
        dropDBs: () => void
        getAllDetailedRecords: () => Promise<any>
        getAllRecords: () => Promise<any>
    }
}
export default StepCounter
