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
exports.FaultProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let FaultProxyController = class FaultProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listFaults(status, severity, page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault`, {
            params: { status, severity, page, size },
        }));
        return data;
    }
    async createFault(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault`, body));
        return data;
    }
    async getFault(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault/${id}`));
        return data;
    }
    async acknowledgeFault(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault/${id}/acknowledge`, body));
        return data;
    }
    async clearFault(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault/${id}/clear`, body));
        return data;
    }
    async closeFault(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault/${id}/close`));
        return data;
    }
};
exports.FaultProxyController = FaultProxyController;
__decorate([
    (0, common_1.Get)('fault'),
    (0, swagger_1.ApiOperation)({ summary: 'List faults' }),
    (0, swagger_1.ApiQuery)({ name: 'status', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'severity', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'page', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'size', required: false }),
    __param(0, (0, common_1.Query)('status')),
    __param(1, (0, common_1.Query)('severity')),
    __param(2, (0, common_1.Query)('page')),
    __param(3, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, String, String]),
    __metadata("design:returntype", Promise)
], FaultProxyController.prototype, "listFaults", null);
__decorate([
    (0, common_1.Post)('fault'),
    (0, swagger_1.ApiOperation)({ summary: 'Create fault' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], FaultProxyController.prototype, "createFault", null);
__decorate([
    (0, common_1.Get)('fault/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get fault' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Fault UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], FaultProxyController.prototype, "getFault", null);
__decorate([
    (0, common_1.Post)('fault/:id/acknowledge'),
    (0, swagger_1.ApiOperation)({ summary: 'Acknowledge fault' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], FaultProxyController.prototype, "acknowledgeFault", null);
__decorate([
    (0, common_1.Post)('fault/:id/clear'),
    (0, swagger_1.ApiOperation)({ summary: 'Clear fault' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], FaultProxyController.prototype, "clearFault", null);
__decorate([
    (0, common_1.Post)('fault/:id/close'),
    (0, swagger_1.ApiOperation)({ summary: 'Close fault' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], FaultProxyController.prototype, "closeFault", null);
exports.FaultProxyController = FaultProxyController = __decorate([
    (0, swagger_1.ApiTags)('OSS - Fault Management'),
    (0, common_1.Controller)('tmf-api/faultManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], FaultProxyController);
//# sourceMappingURL=fault-proxy.controller.js.map