import { HttpService } from '@nestjs/axios';
export declare class ResourceProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listResources(): Promise<any>;
    getResource(id: string): Promise<any>;
    createResource(body: any): Promise<any>;
}
