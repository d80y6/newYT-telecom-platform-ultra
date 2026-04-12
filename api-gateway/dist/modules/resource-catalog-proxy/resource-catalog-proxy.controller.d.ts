import { HttpService } from '@nestjs/axios';
export declare class ResourceCatalogProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    createCatalog(body: any): Promise<any>;
    listCatalogs(page?: string, size?: string, sortBy?: string, sortOrder?: string): Promise<any>;
    getCatalog(id: string): Promise<any>;
    updateCatalog(id: string, body: any): Promise<any>;
    deleteCatalog(id: string): Promise<{
        deleted: boolean;
    }>;
    activateCatalog(id: string): Promise<any>;
    deprecateCatalog(id: string): Promise<any>;
    createSpecification(body: any): Promise<any>;
    listSpecifications(page?: string, size?: string): Promise<any>;
    getSpecificationsByType(resourceType: string): Promise<any>;
    activateSpecification(id: string): Promise<any>;
}
