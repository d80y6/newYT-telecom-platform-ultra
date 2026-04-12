import { HttpService } from '@nestjs/axios';
export declare class AlarmProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    createAlarm(body: any): Promise<any>;
    listAlarms(page?: string, size?: string): Promise<any>;
    getAlarm(id: string): Promise<any>;
    updateAlarm(id: string, body: any): Promise<any>;
    deleteAlarm(id: string): Promise<{
        deleted: boolean;
    }>;
    acknowledgeAlarm(id: string): Promise<any>;
    clearAlarm(id: string): Promise<any>;
    closeAlarm(id: string): Promise<any>;
    getAlarmsByStatus(status: string): Promise<any>;
    getAlarmsBySeverity(severity: string): Promise<any>;
    getAlarmsForResource(resourceId: string): Promise<any>;
}
