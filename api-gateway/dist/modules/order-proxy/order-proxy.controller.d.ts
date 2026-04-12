import { HttpService } from '@nestjs/axios';
export declare class OrderProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listOrders(offset?: string, limit?: string): Promise<any>;
    getOrder(id: string): Promise<any>;
    createOrder(body: any): Promise<any>;
    private mapToTmfResponse;
}
