import { HttpService } from '@nestjs/axios';
export declare class CatalogProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listOfferings(offset?: string, limit?: string): Promise<any>;
    getOffering(id: string): Promise<any>;
    createOffering(body: any): Promise<any>;
    private mapToTmfResponse;
}
