import { HttpService } from '@nestjs/axios';
export declare class ProvisioningProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listProvisioningOrders(status?: string, page?: string, size?: string): Promise<any>;
    createProvisioningOrder(body: any): Promise<any>;
    getProvisioningOrder(id: string): Promise<any>;
    assignTechnician(id: string, body: any): Promise<any>;
    startWork(id: string): Promise<any>;
    completeOrder(id: string, body: any): Promise<any>;
}
