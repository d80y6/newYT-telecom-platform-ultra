import { HttpService } from '@nestjs/axios';
export declare class ProductPriceProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listPrices(offset?: string, limit?: string): Promise<any>;
    getPrice(id: string): Promise<any>;
    createPrice(body: any): Promise<any>;
    updatePrice(id: string, body: any): Promise<any>;
    deletePrice(id: string): Promise<void>;
    private mapToTmfResponse;
}
