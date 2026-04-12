import { HttpService } from '@nestjs/axios';
export declare class ServiceOrderProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listOrders(): Promise<any>;
    getOrder(id: string): Promise<any>;
    createOrder(body: any): Promise<any>;
}
