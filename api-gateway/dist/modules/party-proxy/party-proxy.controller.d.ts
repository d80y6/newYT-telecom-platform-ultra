import { HttpService } from '@nestjs/axios';
export declare class PartyProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listParties(offset?: string, limit?: string): Promise<any>;
    getParty(id: string): Promise<any>;
    createParty(body: any): Promise<any>;
    updateParty(id: string, body: any): Promise<any>;
    deleteParty(id: string): Promise<void>;
    private mapToTmfResponse;
}
