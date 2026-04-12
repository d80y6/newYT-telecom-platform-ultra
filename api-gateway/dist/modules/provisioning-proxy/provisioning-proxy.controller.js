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
exports.ProvisioningProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let ProvisioningProxyController = class ProvisioningProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listProvisioningOrders(status, page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder`, {
            params: { status, page, size },
        }));
        return data;
    }
    async createProvisioningOrder(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder`, body));
        return data;
    }
    async getProvisioningOrder(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder/${id}`));
        return data;
    }
    async assignTechnician(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder/${id}/assign`, body));
        return data;
    }
    async startWork(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder/${id}/start`));
        return data;
    }
    async completeOrder(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder/${id}/complete`, body));
        return data;
    }
};
exports.ProvisioningProxyController = ProvisioningProxyController;
__decorate([
    (0, common_1.Get)('provisioningOrder'),
    (0, swagger_1.ApiOperation)({ summary: 'List provisioning orders' }),
    (0, swagger_1.ApiQuery)({ name: 'status', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'page', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'size', required: false }),
    __param(0, (0, common_1.Query)('status')),
    __param(1, (0, common_1.Query)('page')),
    __param(2, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, String]),
    __metadata("design:returntype", Promise)
], ProvisioningProxyController.prototype, "listProvisioningOrders", null);
__decorate([
    (0, common_1.Post)('provisioningOrder'),
    (0, swagger_1.ApiOperation)({ summary: 'Create provisioning order' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], ProvisioningProxyController.prototype, "createProvisioningOrder", null);
__decorate([
    (0, common_1.Get)('provisioningOrder/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get provisioning order' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Order UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ProvisioningProxyController.prototype, "getProvisioningOrder", null);
__decorate([
    (0, common_1.Post)('provisioningOrder/:id/assign'),
    (0, swagger_1.ApiOperation)({ summary: 'Assign technician' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], ProvisioningProxyController.prototype, "assignTechnician", null);
__decorate([
    (0, common_1.Post)('provisioningOrder/:id/start'),
    (0, swagger_1.ApiOperation)({ summary: 'Start work' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ProvisioningProxyController.prototype, "startWork", null);
__decorate([
    (0, common_1.Post)('provisioningOrder/:id/complete'),
    (0, swagger_1.ApiOperation)({ summary: 'Complete order' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], ProvisioningProxyController.prototype, "completeOrder", null);
exports.ProvisioningProxyController = ProvisioningProxyController = __decorate([
    (0, swagger_1.ApiTags)('OSS - Network Provisioning'),
    (0, common_1.Controller)('tmf-api/serviceProvisioningManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], ProvisioningProxyController);
//# sourceMappingURL=provisioning-proxy.controller.js.map