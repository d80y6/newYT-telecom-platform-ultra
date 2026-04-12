import { HttpService } from '@nestjs/axios';
export declare class PricePlanProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listPricePlans(offset?: string, limit?: string): Promise<any>;
    getPricePlan(id: string): Promise<any>;
    createPricePlan(body: any): Promise<any>;
    updatePricePlan(id: string, body: any): Promise<any>;
    private mapToTmfResponse;
}
