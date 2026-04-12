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
exports.AnalyticsProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const ANALYTICS_SERVICE = process.env.ANALYTICS_SERVICE_URL || 'http://bss-core:8080/api/v1';
let AnalyticsProxyController = class AnalyticsProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listMetrics(category, page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/metric`, {
            params: { category, page, size },
        }));
        return this.mapToTmfResponse(data);
    }
    async getMetric(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/metric/${id}`));
        return this.mapToTmfResponse(data);
    }
    async createMetric(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${ANALYTICS_SERVICE}/analytics/v5/metric`, body));
        return this.mapToTmfResponse(data);
    }
    async getDashboard() {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/dashboard`));
        return data;
    }
    async getKpis() {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/kpi`));
        return data;
    }
    async getTimeSeries(metricName, startTime, endTime) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/timeSeries`, {
            params: { metricName, startTime, endTime },
        }));
        return this.mapToTmfResponse(data);
    }
    mapToTmfResponse(data) {
        return {
            '@type': 'AnalyticsMetric',
            '@baseType': 'BaseEntity',
            ...data,
        };
    }
};
exports.AnalyticsProxyController = AnalyticsProxyController;
__decorate([
    (0, common_1.Get)('/metric'),
    (0, swagger_1.ApiOperation)({ summary: 'List analytics metrics' }),
    __param(0, (0, common_1.Query)('category')),
    __param(1, (0, common_1.Query)('page')),
    __param(2, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, String]),
    __metadata("design:returntype", Promise)
], AnalyticsProxyController.prototype, "listMetrics", null);
__decorate([
    (0, common_1.Get)('/metric/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Retrieve an analytics metric' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Metric UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AnalyticsProxyController.prototype, "getMetric", null);
__decorate([
    (0, common_1.Post)('/metric'),
    (0, swagger_1.ApiOperation)({ summary: 'Create an analytics metric' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], AnalyticsProxyController.prototype, "createMetric", null);
__decorate([
    (0, common_1.Get)('/dashboard'),
    (0, swagger_1.ApiOperation)({ summary: 'Get dashboard summary' }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], AnalyticsProxyController.prototype, "getDashboard", null);
__decorate([
    (0, common_1.Get)('/kpi'),
    (0, swagger_1.ApiOperation)({ summary: 'Get KPIs' }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], AnalyticsProxyController.prototype, "getKpis", null);
__decorate([
    (0, common_1.Get)('/timeSeries'),
    (0, swagger_1.ApiOperation)({ summary: 'Get time series data' }),
    __param(0, (0, common_1.Query)('metricName')),
    __param(1, (0, common_1.Query)('startTime')),
    __param(2, (0, common_1.Query)('endTime')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, String]),
    __metadata("design:returntype", Promise)
], AnalyticsProxyController.prototype, "getTimeSeries", null);
exports.AnalyticsProxyController = AnalyticsProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMFxxx - Analytics'),
    (0, common_1.Controller)('tmf-api/analytics/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], AnalyticsProxyController);
//# sourceMappingURL=analytics-proxy.controller.js.map