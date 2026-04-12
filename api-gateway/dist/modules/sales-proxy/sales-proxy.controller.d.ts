import { HttpService } from '@nestjs/axios';
export declare class SalesProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listLeads(offset?: string, limit?: string): Promise<any>;
    getLead(id: string): Promise<any>;
    createLead(body: any): Promise<any>;
    updateLeadStatus(id: string, body: any): Promise<any>;
    private mapToTmfResponse;
}
