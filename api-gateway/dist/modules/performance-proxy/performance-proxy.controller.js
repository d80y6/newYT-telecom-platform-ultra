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
exports.PerformanceProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let PerformanceProxyController = class PerformanceProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listMetrics(resourceId, severity, page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/performanceManagement/v5/performanceMetric`, {
            params: { resourceId, severity, page, size },
        }));
        return data;
    }
    async createMetric(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/performanceManagement/v5/performanceMetric`, body));
        return data;
    }
    async getMetric(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/performanceManagement/v5/performanceMetric/${id}`));
        return data;
    }
    async generateReport(period, resourceId) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/performanceManagement/v5/performanceReport`, {
            params: { period, resourceId },
        }));
        return data;
    }
};
exports.PerformanceProxyController = PerformanceProxyController;
__decorate([
    (0, common_1.Get)('performanceMetric'),
    (0, swagger_1.ApiOperation)({ summary: 'List performance metrics' }),
    (0, swagger_1.ApiQuery)({ name: 'resourceId', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'severity', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'page', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'size', required: false }),
    __param(0, (0, common_1.Query)('resourceId')),
    __param(1, (0, common_1.Query)('severity')),
    __param(2, (0, common_1.Query)('page')),
    __param(3, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, String, String]),
    __metadata("design:returntype", Promise)
], PerformanceProxyController.prototype, "listMetrics", null);
__decorate([
    (0, common_1.Post)('performanceMetric'),
    (0, swagger_1.ApiOperation)({ summary: 'Create performance metric' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], PerformanceProxyController.prototype, "createMetric", null);
__decorate([
    (0, common_1.Get)('performanceMetric/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get performance metric' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Metric UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], PerformanceProxyController.prototype, "getMetric", null);
__decorate([
    (0, common_1.Get)('performanceReport'),
    (0, swagger_1.ApiOperation)({ summary: 'Generate performance report' }),
    (0, swagger_1.ApiQuery)({ name: 'period', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'resourceId', required: false }),
    __param(0, (0, common_1.Query)('period')),
    __param(1, (0, common_1.Query)('resourceId')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], PerformanceProxyController.prototype, "generateReport", null);
exports.PerformanceProxyController = PerformanceProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF672 - Performance Management'),
    (0, common_1.Controller)('tmf-api/performanceManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], PerformanceProxyController);
//# sourceMappingURL=performance-proxy.controller.js.map