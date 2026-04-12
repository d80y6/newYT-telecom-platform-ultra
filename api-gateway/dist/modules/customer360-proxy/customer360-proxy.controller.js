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
exports.Customer360ProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let Customer360ProxyController = class Customer360ProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listCustomers360(segment, customerType, status, page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer`, {
            params: { segment, customerType, status, page, size },
        }));
        return this.mapToTmfResponse(data);
    }
    async createCustomer360(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer`, body));
        return this.mapToTmfResponse(data);
    }
    async getCustomer360(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}`));
        return this.mapToTmfResponse(data);
    }
    async getCustomerSegments(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/segment`));
        return this.mapToTmfResponse(data);
    }
    async assignSegment(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/segment`, body));
        return this.mapToTmfResponse(data);
    }
    async getAccountHierarchy(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/hierarchy`));
        return this.mapToTmfResponse(data);
    }
    async addAccountRelationship(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/hierarchy`, body));
        return this.mapToTmfResponse(data);
    }
    async updateEngagement(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/engagement`, body));
        return this.mapToTmfResponse(data);
    }
    mapToTmfResponse(data) {
        return data;
    }
};
exports.Customer360ProxyController = Customer360ProxyController;
__decorate([
    (0, common_1.Get)('customer'),
    (0, swagger_1.ApiOperation)({ summary: 'List customers with 360 view' }),
    (0, swagger_1.ApiQuery)({ name: 'segment', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'customerType', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'status', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'page', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'size', required: false }),
    __param(0, (0, common_1.Query)('segment')),
    __param(1, (0, common_1.Query)('customerType')),
    __param(2, (0, common_1.Query)('status')),
    __param(3, (0, common_1.Query)('page')),
    __param(4, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, String, String, String]),
    __metadata("design:returntype", Promise)
], Customer360ProxyController.prototype, "listCustomers360", null);
__decorate([
    (0, common_1.Post)('customer'),
    (0, swagger_1.ApiOperation)({ summary: 'Create customer with full profile' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], Customer360ProxyController.prototype, "createCustomer360", null);
__decorate([
    (0, common_1.Get)('customer/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get customer 360 view' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Customer UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], Customer360ProxyController.prototype, "getCustomer360", null);
__decorate([
    (0, common_1.Get)('customer/:id/segment'),
    (0, swagger_1.ApiOperation)({ summary: 'Get customer segments' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Customer UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], Customer360ProxyController.prototype, "getCustomerSegments", null);
__decorate([
    (0, common_1.Post)('customer/:id/segment'),
    (0, swagger_1.ApiOperation)({ summary: 'Assign segment to customer' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Customer UUID' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], Customer360ProxyController.prototype, "assignSegment", null);
__decorate([
    (0, common_1.Get)('customer/:id/hierarchy'),
    (0, swagger_1.ApiOperation)({ summary: 'Get account hierarchy' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Customer UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], Customer360ProxyController.prototype, "getAccountHierarchy", null);
__decorate([
    (0, common_1.Post)('customer/:id/hierarchy'),
    (0, swagger_1.ApiOperation)({ summary: 'Add account relationship' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Customer UUID' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], Customer360ProxyController.prototype, "addAccountRelationship", null);
__decorate([
    (0, common_1.Patch)('customer/:id/engagement'),
    (0, swagger_1.ApiOperation)({ summary: 'Update engagement score' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], Customer360ProxyController.prototype, "updateEngagement", null);
exports.Customer360ProxyController = Customer360ProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF629 - Customer Management'),
    (0, common_1.Controller)('tmf-api/customerManagement/v4'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], Customer360ProxyController);
//# sourceMappingURL=customer360-proxy.controller.js.map