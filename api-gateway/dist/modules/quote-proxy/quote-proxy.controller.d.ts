import { HttpService } from '@nestjs/axios';
export declare class QuoteProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listQuotes(offset?: string, limit?: string): Promise<any>;
    getQuote(id: string): Promise<any>;
    createQuote(body: any): Promise<any>;
    updateQuoteStatus(id: string, body: any): Promise<any>;
    private mapToTmfResponse;
}
