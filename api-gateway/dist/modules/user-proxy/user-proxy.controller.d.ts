import { HttpService } from '@nestjs/axios';
export declare class UserProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    createUser(body: any): Promise<any>;
    listUsers(page?: string, size?: string): Promise<any>;
    getUser(id: string): Promise<any>;
    updateUser(id: string, body: any): Promise<any>;
    deleteUser(id: string): Promise<{
        deleted: boolean;
    }>;
    suspendUser(id: string): Promise<any>;
    activateUser(id: string): Promise<any>;
}
