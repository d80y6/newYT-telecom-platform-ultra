import { HttpService } from '@nestjs/axios';
export declare class FaultProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listFaults(status?: string, severity?: string, page?: string, size?: string): Promise<any>;
    createFault(body: any): Promise<any>;
    getFault(id: string): Promise<any>;
    acknowledgeFault(id: string, body: any): Promise<any>;
    clearFault(id: string, body: any): Promise<any>;
    closeFault(id: string): Promise<any>;
}
