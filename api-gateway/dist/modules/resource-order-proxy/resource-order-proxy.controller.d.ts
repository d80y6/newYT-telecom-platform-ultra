import { HttpService } from '@nestjs/axios';
export declare class ResourceOrderProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listResourceOrders(state?: string, orderType?: string, priority?: string, resourceType?: string, page?: string, size?: string): Promise<any>;
    createResourceOrder(body: any): Promise<any>;
    getResourceOrder(id: string): Promise<any>;
    updateResourceOrder(id: string, body: any): Promise<any>;
    changeState(id: string, body: any): Promise<any>;
    acknowledge(id: string): Promise<any>;
    complete(id: string): Promise<any>;
    private mapToTmfResponse;
}
