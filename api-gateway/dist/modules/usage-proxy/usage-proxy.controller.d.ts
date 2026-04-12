import { HttpService } from '@nestjs/axios';
export declare class UsageProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listUsage(): Promise<any>;
    getUsage(id: string): Promise<any>;
    createUsage(body: any): Promise<any>;
}
