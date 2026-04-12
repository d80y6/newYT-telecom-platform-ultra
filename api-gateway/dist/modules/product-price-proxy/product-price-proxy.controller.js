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
exports.ProductPriceProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const PRICE_SERVICE = process.env.PRICE_SERVICE_URL || 'http://bss-core:8080/api/v1';
let ProductPriceProxyController = class ProductPriceProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listPrices(offset, limit) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${PRICE_SERVICE}/product-price`, {
            params: { offset, limit },
        }));
        return this.mapToTmfResponse(data);
    }
    async getPrice(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${PRICE_SERVICE}/product-price/${id}`));
        return this.mapToTmfResponse(data);
    }
    async createPrice(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${PRICE_SERVICE}/product-price`, body));
        return this.mapToTmfResponse(data);
    }
    async updatePrice(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.put(`${PRICE_SERVICE}/product-price/${id}`, body));
        return this.mapToTmfResponse(data);
    }
    async deletePrice(id) {
        await (0, rxjs_1.firstValueFrom)(this.httpService.delete(`${PRICE_SERVICE}/product-price/${id}`));
    }
    mapToTmfResponse(data) {
        return {
            '@type': 'ProductPrice',
            '@baseType': 'BaseEntity',
            ...data,
        };
    }
};
exports.ProductPriceProxyController = ProductPriceProxyController;
__decorate([
    (0, common_1.Get)(),
    (0, swagger_1.ApiOperation)({ summary: 'List product prices' }),
    __param(0, (0, common_1.Query)('offset')),
    __param(1, (0, common_1.Query)('limit')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], ProductPriceProxyController.prototype, "listPrices", null);
__decorate([
    (0, common_1.Get)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Retrieve a product price' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Price UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ProductPriceProxyController.prototype, "getPrice", null);
__decorate([
    (0, common_1.Post)(),
    (0, swagger_1.ApiOperation)({ summary: 'Create a product price' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], ProductPriceProxyController.prototype, "createPrice", null);
__decorate([
    (0, common_1.Put)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update a product price' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], ProductPriceProxyController.prototype, "updatePrice", null);
__decorate([
    (0, common_1.Delete)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Delete a product price' }),
    (0, common_1.HttpCode)(204),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ProductPriceProxyController.prototype, "deletePrice", null);
exports.ProductPriceProxyController = ProductPriceProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF625 - Product Pricing'),
    (0, common_1.Controller)('tmf-api/productCatalogManagement/v5/productPrice'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], ProductPriceProxyController);
//# sourceMappingURL=product-price-proxy.controller.js.map