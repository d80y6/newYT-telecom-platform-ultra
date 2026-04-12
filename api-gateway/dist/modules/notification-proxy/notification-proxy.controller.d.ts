import { HttpService } from '@nestjs/axios';
export declare class NotificationProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listNotifications(): Promise<any>;
    createNotification(body: any): Promise<any>;
}
