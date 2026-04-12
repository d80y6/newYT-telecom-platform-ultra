import { HttpService } from '@nestjs/axios';
export declare class FraudProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listAlerts(): Promise<any>;
    createAlert(body: any): Promise<any>;
    getAlert(id: string): Promise<any>;
    confirmAlert(id: string, body: any): Promise<any>;
}
