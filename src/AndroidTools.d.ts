declare const AndroidTools: {
    startBackgroundService: () => Promise<any>
    stopBackgroundService: () => void
    isActiveBackgroundService: () => Promise<any>
    dropDBs: () => void
    getAllDetailedRecords: () => Promise<any>
    getAllRecords: () => Promise<any>
}
export default AndroidTools
