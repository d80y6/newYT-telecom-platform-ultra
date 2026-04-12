import { HttpService } from '@nestjs/axios';
export declare class InventoryProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listInventory(offset?: string, limit?: string): Promise<any>;
    getInventory(id: string): Promise<any>;
    createInventory(body: any): Promise<any>;
}
