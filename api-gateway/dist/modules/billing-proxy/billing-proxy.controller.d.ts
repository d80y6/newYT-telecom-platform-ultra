import { HttpService } from '@nestjs/axios';
export declare class BillingProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listBills(): Promise<any>;
    getBill(id: string): Promise<any>;
    createBill(body: any): Promise<any>;
}
