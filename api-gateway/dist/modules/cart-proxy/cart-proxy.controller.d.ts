import { HttpService } from '@nestjs/axios';
export declare class CartProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    listCarts(offset?: string, limit?: string): Promise<any>;
    getCart(id: string): Promise<any>;
    createCart(body: any): Promise<any>;
    updateCartStatus(id: string, body: any): Promise<any>;
    private mapToTmfResponse;
}
