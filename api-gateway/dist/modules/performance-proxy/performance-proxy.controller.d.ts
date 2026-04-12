import { HttpService } from '@nestjs/axios';
export declare class PerformanceProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listMetrics(resourceId?: string, severity?: string, page?: string, size?: string): Promise<any>;
    createMetric(body: any): Promise<any>;
    getMetric(id: string): Promise<any>;
    generateReport(period?: string, resourceId?: string): Promise<any>;
}
