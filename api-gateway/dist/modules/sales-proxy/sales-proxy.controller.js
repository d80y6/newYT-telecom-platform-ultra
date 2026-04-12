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
exports.SalesProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const SALES_SERVICE = process.env.SALES_SERVICE_URL || 'http://bss-core:8080/api/v1';
let SalesProxyController = class SalesProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listLeads(offset, limit) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${SALES_SERVICE}/sales-lead`, {
            params: { offset, limit },
        }));
        return this.mapToTmfResponse(data);
    }
    async getLead(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${SALES_SERVICE}/sales-lead/${id}`));
        return this.mapToTmfResponse(data);
    }
    async createLead(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${SALES_SERVICE}/sales-lead`, body));
        return this.mapToTmfResponse(data);
    }
    async updateLeadStatus(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${SALES_SERVICE}/sales-lead/${id}/status`, body));
        return this.mapToTmfResponse(data);
    }
    mapToTmfResponse(data) {
        return {
            '@type': 'SalesLead',
            '@baseType': 'BaseEntity',
            ...data,
        };
    }
};
exports.SalesProxyController = SalesProxyController;
__decorate([
    (0, common_1.Get)(),
    (0, swagger_1.ApiOperation)({ summary: 'List sales leads' }),
    __param(0, (0, common_1.Query)('offset')),
    __param(1, (0, common_1.Query)('limit')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], SalesProxyController.prototype, "listLeads", null);
__decorate([
    (0, common_1.Get)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Retrieve a sales lead' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Lead UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], SalesProxyController.prototype, "getLead", null);
__decorate([
    (0, common_1.Post)(),
    (0, swagger_1.ApiOperation)({ summary: 'Create a sales lead' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], SalesProxyController.prototype, "createLead", null);
__decorate([
    (0, common_1.Patch)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update sales lead status' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], SalesProxyController.prototype, "updateLeadStatus", null);
exports.SalesProxyController = SalesProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF699 - Sales Lead'),
    (0, common_1.Controller)('tmf-api/salesLeadManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], SalesProxyController);
//# sourceMappingURL=sales-proxy.controller.js.map