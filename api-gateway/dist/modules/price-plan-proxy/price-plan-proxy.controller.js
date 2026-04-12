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
exports.PricePlanProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const PRICE_SERVICE = process.env.PRICE_SERVICE_URL || 'http://bss-core:8080/api/v1';
let PricePlanProxyController = class PricePlanProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listPricePlans(offset, limit) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${PRICE_SERVICE}/price-plan`, {
            params: { offset, limit },
        }));
        return this.mapToTmfResponse(data);
    }
    async getPricePlan(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${PRICE_SERVICE}/price-plan/${id}`));
        return this.mapToTmfResponse(data);
    }
    async createPricePlan(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${PRICE_SERVICE}/price-plan`, body));
        return this.mapToTmfResponse(data);
    }
    async updatePricePlan(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.put(`${PRICE_SERVICE}/price-plan/${id}`, body));
        return this.mapToTmfResponse(data);
    }
    mapToTmfResponse(data) {
        return {
            '@type': 'PricePlan',
            '@baseType': 'BaseEntity',
            ...data,
        };
    }
};
exports.PricePlanProxyController = PricePlanProxyController;
__decorate([
    (0, common_1.Get)(),
    (0, swagger_1.ApiOperation)({ summary: 'List price plans (recurring)' }),
    __param(0, (0, common_1.Query)('offset')),
    __param(1, (0, common_1.Query)('limit')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], PricePlanProxyController.prototype, "listPricePlans", null);
__decorate([
    (0, common_1.Get)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Retrieve a price plan' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Plan UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], PricePlanProxyController.prototype, "getPricePlan", null);
__decorate([
    (0, common_1.Post)(),
    (0, swagger_1.ApiOperation)({ summary: 'Create a price plan' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], PricePlanProxyController.prototype, "createPricePlan", null);
__decorate([
    (0, common_1.Put)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update a price plan' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], PricePlanProxyController.prototype, "updatePricePlan", null);
exports.PricePlanProxyController = PricePlanProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF655 - Price Recurring'),
    (0, common_1.Controller)('tmf-api/productCatalogManagement/v5/pricePlan'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], PricePlanProxyController);
//# sourceMappingURL=price-plan-proxy.controller.js.map