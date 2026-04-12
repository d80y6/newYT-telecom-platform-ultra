import { HttpService } from '@nestjs/axios';
export declare class IdentityProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    createIdentity(body: any): Promise<any>;
    listIdentities(page?: string, size?: string): Promise<any>;
    getIdentity(id: string): Promise<any>;
    updateIdentity(id: string, body: any): Promise<any>;
    deleteIdentity(id: string): Promise<{
        deleted: boolean;
    }>;
    verifyIdentity(id: string): Promise<any>;
    setPrimary(id: string): Promise<any>;
    revokeIdentity(id: string): Promise<any>;
    getIdentitiesByParty(partyId: string): Promise<any>;
}
