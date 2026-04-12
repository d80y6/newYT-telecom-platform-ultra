import { HttpService } from '@nestjs/axios';
export declare class ChargingProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    getBalance(id: string): Promise<any>;
    reserveBalance(id: string, body: any): Promise<any>;
    confirmDeduction(id: string, body: any): Promise<any>;
}
