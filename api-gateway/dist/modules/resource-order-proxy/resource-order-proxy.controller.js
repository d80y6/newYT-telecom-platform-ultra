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
exports.ResourceOrderProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let ResourceOrderProxyController = class ResourceOrderProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listResourceOrders(state, orderType, priority, resourceType, page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder`, {
            params: { state, orderType, priority, resourceType, page, size },
        }));
        return this.mapToTmfResponse(data);
    }
    async createResourceOrder(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder`, body));
        return this.mapToTmfResponse(data);
    }
    async getResourceOrder(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}`));
        return this.mapToTmfResponse(data);
    }
    async updateResourceOrder(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}`, body));
        return this.mapToTmfResponse(data);
    }
    async changeState(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}/stateChange`, body));
        return this.mapToTmfResponse(data);
    }
    async acknowledge(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}/acknowledge`));
        return this.mapToTmfResponse(data);
    }
    async complete(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}/complete`));
        return this.mapToTmfResponse(data);
    }
    mapToTmfResponse(data) {
        return data;
    }
};
exports.ResourceOrderProxyController = ResourceOrderProxyController;
__decorate([
    (0, common_1.Get)('resourceOrder'),
    (0, swagger_1.ApiOperation)({ summary: 'List resource orders' }),
    (0, swagger_1.ApiQuery)({ name: 'state', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'orderType', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'priority', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'resourceType', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'page', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'size', required: false }),
    __param(0, (0, common_1.Query)('state')),
    __param(1, (0, common_1.Query)('orderType')),
    __param(2, (0, common_1.Query)('priority')),
    __param(3, (0, common_1.Query)('resourceType')),
    __param(4, (0, common_1.Query)('page')),
    __param(5, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, String, String, String, String]),
    __metadata("design:returntype", Promise)
], ResourceOrderProxyController.prototype, "listResourceOrders", null);
__decorate([
    (0, common_1.Post)('resourceOrder'),
    (0, swagger_1.ApiOperation)({ summary: 'Create resource order' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], ResourceOrderProxyController.prototype, "createResourceOrder", null);
__decorate([
    (0, common_1.Get)('resourceOrder/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get resource order' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Resource Order UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ResourceOrderProxyController.prototype, "getResourceOrder", null);
__decorate([
    (0, common_1.Patch)('resourceOrder/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update resource order' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], ResourceOrderProxyController.prototype, "updateResourceOrder", null);
__decorate([
    (0, common_1.Post)('resourceOrder/:id/stateChange'),
    (0, swagger_1.ApiOperation)({ summary: 'Change resource order state' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], ResourceOrderProxyController.prototype, "changeState", null);
__decorate([
    (0, common_1.Post)('resourceOrder/:id/acknowledge'),
    (0, swagger_1.ApiOperation)({ summary: 'Acknowledge resource order' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ResourceOrderProxyController.prototype, "acknowledge", null);
__decorate([
    (0, common_1.Post)('resourceOrder/:id/complete'),
    (0, swagger_1.ApiOperation)({ summary: 'Complete resource order' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ResourceOrderProxyController.prototype, "complete", null);
exports.ResourceOrderProxyController = ResourceOrderProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF640 - Resource Order Management'),
    (0, common_1.Controller)('tmf-api/resourceOrderingManagement/v4'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], ResourceOrderProxyController);
//# sourceMappingURL=resource-order-proxy.controller.js.map