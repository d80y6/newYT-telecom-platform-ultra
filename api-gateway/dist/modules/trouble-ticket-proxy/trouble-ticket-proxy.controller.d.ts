import { HttpService } from '@nestjs/axios';
export declare class TroubleTicketProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listTickets(): Promise<any>;
    getTicket(id: string): Promise<any>;
    createTicket(body: any): Promise<any>;
}
