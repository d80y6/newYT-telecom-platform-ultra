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
exports.FraudProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let FraudProxyController = class FraudProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listAlerts() {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/fraudManagement/v5/alert`));
        return data;
    }
    async createAlert(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/fraudManagement/v5/alert`, body));
        return data;
    }
    async getAlert(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/fraudManagement/v5/alert/${id}`));
        return data;
    }
    async confirmAlert(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/fraudManagement/v5/alert/${id}/confirm`, body));
        return data;
    }
};
exports.FraudProxyController = FraudProxyController;
__decorate([
    (0, common_1.Get)('alert'),
    (0, swagger_1.ApiOperation)({ summary: 'List fraud alerts' }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], FraudProxyController.prototype, "listAlerts", null);
__decorate([
    (0, common_1.Post)('alert'),
    (0, common_1.HttpCode)(201),
    (0, swagger_1.ApiOperation)({ summary: 'Create fraud alert' }),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], FraudProxyController.prototype, "createAlert", null);
__decorate([
    (0, common_1.Get)('alert/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get fraud alert' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Alert UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], FraudProxyController.prototype, "getAlert", null);
__decorate([
    (0, common_1.Post)('alert/:id/confirm'),
    (0, swagger_1.ApiOperation)({ summary: 'Confirm fraud alert' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], FraudProxyController.prototype, "confirmAlert", null);
exports.FraudProxyController = FraudProxyController = __decorate([
    (0, swagger_1.ApiTags)('Fraud Detection'),
    (0, common_1.Controller)('tmf-api/fraudManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], FraudProxyController);
//# sourceMappingURL=fraud-proxy.controller.js.map