import { HttpService } from '@nestjs/axios';
export declare class AnalyticsProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listMetrics(category?: string, page?: string, size?: string): Promise<any>;
    getMetric(id: string): Promise<any>;
    createMetric(body: any): Promise<any>;
    getDashboard(): Promise<any>;
    getKpis(): Promise<any>;
    getTimeSeries(metricName: string, startTime: string, endTime: string): Promise<any>;
    private mapToTmfResponse;
}
