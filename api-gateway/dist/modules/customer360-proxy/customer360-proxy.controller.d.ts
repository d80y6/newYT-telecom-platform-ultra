import { HttpService } from '@nestjs/axios';
export declare class Customer360ProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listCustomers360(segment?: string, customerType?: string, status?: string, page?: string, size?: string): Promise<any>;
    createCustomer360(body: any): Promise<any>;
    getCustomer360(id: string): Promise<any>;
    getCustomerSegments(id: string): Promise<any>;
    assignSegment(id: string, body: any): Promise<any>;
    getAccountHierarchy(id: string): Promise<any>;
    addAccountRelationship(id: string, body: any): Promise<any>;
    updateEngagement(id: string, body: any): Promise<any>;
    private mapToTmfResponse;
}
