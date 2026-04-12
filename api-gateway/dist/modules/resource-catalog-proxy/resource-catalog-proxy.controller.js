"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.ResourceCatalogProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let ResourceCatalogProxyController = class ResourceCatalogProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async createCatalog(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog`, body));
        return data;
    }
    async listCatalogs(page, size, sortBy, sortOrder) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog`, {
            params: { page, size, sortBy, sortOrder },
        }));
        return data;
    }
    async getCatalog(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}`));
        return data;
    }
    async updateCatalog(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.put(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}`, body));
        return data;
    }
    async deleteCatalog(id) {
        await (0, rxjs_1.firstValueFrom)(this.httpService.delete(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}`));
        return { deleted: true };
    }
    async activateCatalog(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}/activate`, {}));
        return data;
    }
    async deprecateCatalog(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}/deprecate`, {}));
        return data;
    }
    async createSpecification(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceSpecification`, body));
        return data;
    }
    async listSpecifications(page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceSpecification`, {
            params: { page, size },
        }));
        return data;
    }
    async getSpecificationsByType(resourceType) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceSpecification/resourceType/${resourceType}`));
        return data;
    }
    async activateSpecification(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceSpecification/${id}/activate`, {}));
        return data;
    }
};
exports.ResourceCatalogProxyController = ResourceCatalogProxyController;
__decorate([
    (0, common_1.Post)('resourceCatalog'),
    (0, swagger_1.ApiOperation)({ summary: 'Create resource catalog' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "createCatalog", null);
__decorate([
    (0, common_1.Get)('resourceCatalog'),
    (0, swagger_1.ApiOperation)({ summary: 'List resource catalogs' }),
    (0, swagger_1.ApiQuery)({ name: 'page', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'size', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'sortBy', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'sortOrder', required: false }),
    __param(0, (0, common_1.Query)('page')),
    __param(1, (0, common_1.Query)('size')),
    __param(2, (0, common_1.Query)('sortBy')),
    __param(3, (0, common_1.Query)('sortOrder')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, String, String]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "listCatalogs", null);
__decorate([
    (0, common_1.Get)('resourceCatalog/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get resource catalog' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Catalog UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "getCatalog", null);
__decorate([
    (0, common_1.Put)('resourceCatalog/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update resource catalog' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Catalog UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "updateCatalog", null);
__decorate([
    (0, common_1.Delete)('resourceCatalog/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Delete resource catalog' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Catalog UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "deleteCatalog", null);
__decorate([
    (0, common_1.Patch)('resourceCatalog/:id/activate'),
    (0, swagger_1.ApiOperation)({ summary: 'Activate resource catalog' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Catalog UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "activateCatalog", null);
__decorate([
    (0, common_1.Patch)('resourceCatalog/:id/deprecate'),
    (0, swagger_1.ApiOperation)({ summary: 'Deprecate resource catalog' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Catalog UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "deprecateCatalog", null);
__decorate([
    (0, common_1.Post)('resourceSpecification'),
    (0, swagger_1.ApiOperation)({ summary: 'Create resource specification' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "createSpecification", null);
__decorate([
    (0, common_1.Get)('resourceSpecification'),
    (0, swagger_1.ApiOperation)({ summary: 'List resource specifications' }),
    (0, swagger_1.ApiQuery)({ name: 'page', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'size', required: false }),
    __param(0, (0, common_1.Query)('page')),
    __param(1, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "listSpecifications", null);
__decorate([
    (0, common_1.Get)('resourceSpecification/resourceType/:resourceType'),
    (0, swagger_1.ApiOperation)({ summary: 'Get specifications by resource type' }),
    (0, swagger_1.ApiParam)({ name: 'resourceType', description: 'Resource type' }),
    __param(0, (0, common_1.Param)('resourceType')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "getSpecificationsByType", null);
__decorate([
    (0, common_1.Patch)('resourceSpecification/:id/activate'),
    (0, swagger_1.ApiOperation)({ summary: 'Activate resource specification' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Specification UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ResourceCatalogProxyController.prototype, "activateSpecification", null);
exports.ResourceCatalogProxyController = ResourceCatalogProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF634 - Resource Catalog Management'),
    (0, common_1.Controller)('tmf-api/resourceCatalogManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], ResourceCatalogProxyController);
//# sourceMappingURL=resource-catalog-proxy.controller.js.map