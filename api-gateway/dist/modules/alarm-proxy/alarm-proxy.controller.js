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
exports.AlarmProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let AlarmProxyController = class AlarmProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async createAlarm(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm`, body));
        return data;
    }
    async listAlarms(page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm`, {
            params: { page, size },
        }));
        return data;
    }
    async getAlarm(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}`));
        return data;
    }
    async updateAlarm(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.put(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}`, body));
        return data;
    }
    async deleteAlarm(id) {
        await (0, rxjs_1.firstValueFrom)(this.httpService.delete(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}`));
        return { deleted: true };
    }
    async acknowledgeAlarm(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}/acknowledge`, {}));
        return data;
    }
    async clearAlarm(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}/clear`, {}));
        return data;
    }
    async closeAlarm(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}/close`, {}));
        return data;
    }
    async getAlarmsByStatus(status) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/status/${status}`));
        return data;
    }
    async getAlarmsBySeverity(severity) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/severity/${severity}`));
        return data;
    }
    async getAlarmsForResource(resourceId) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/resource/${resourceId}`));
        return data;
    }
};
exports.AlarmProxyController = AlarmProxyController;
__decorate([
    (0, common_1.Post)('alarm'),
    (0, swagger_1.ApiOperation)({ summary: 'Create alarm' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "createAlarm", null);
__decorate([
    (0, common_1.Get)('alarm'),
    (0, swagger_1.ApiOperation)({ summary: 'List alarms' }),
    (0, swagger_1.ApiQuery)({ name: 'page', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'size', required: false }),
    __param(0, (0, common_1.Query)('page')),
    __param(1, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "listAlarms", null);
__decorate([
    (0, common_1.Get)('alarm/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get alarm' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Alarm UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "getAlarm", null);
__decorate([
    (0, common_1.Put)('alarm/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update alarm' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Alarm UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "updateAlarm", null);
__decorate([
    (0, common_1.Delete)('alarm/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Delete alarm' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Alarm UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "deleteAlarm", null);
__decorate([
    (0, common_1.Patch)('alarm/:id/acknowledge'),
    (0, swagger_1.ApiOperation)({ summary: 'Acknowledge alarm' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Alarm UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "acknowledgeAlarm", null);
__decorate([
    (0, common_1.Patch)('alarm/:id/clear'),
    (0, swagger_1.ApiOperation)({ summary: 'Clear alarm' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Alarm UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "clearAlarm", null);
__decorate([
    (0, common_1.Patch)('alarm/:id/close'),
    (0, swagger_1.ApiOperation)({ summary: 'Close alarm' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Alarm UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "closeAlarm", null);
__decorate([
    (0, common_1.Get)('alarm/status/:status'),
    (0, swagger_1.ApiOperation)({ summary: 'Get alarms by status' }),
    (0, swagger_1.ApiParam)({ name: 'status', description: 'Alarm status' }),
    __param(0, (0, common_1.Param)('status')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "getAlarmsByStatus", null);
__decorate([
    (0, common_1.Get)('alarm/severity/:severity'),
    (0, swagger_1.ApiOperation)({ summary: 'Get alarms by severity' }),
    (0, swagger_1.ApiParam)({ name: 'severity', description: 'Alarm severity' }),
    __param(0, (0, common_1.Param)('severity')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "getAlarmsBySeverity", null);
__decorate([
    (0, common_1.Get)('alarm/resource/:resourceId'),
    (0, swagger_1.ApiOperation)({ summary: 'Get alarms for resource' }),
    (0, swagger_1.ApiParam)({ name: 'resourceId', description: 'Resource UUID' }),
    __param(0, (0, common_1.Param)('resourceId')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AlarmProxyController.prototype, "getAlarmsForResource", null);
exports.AlarmProxyController = AlarmProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF654 - Alarm Management'),
    (0, common_1.Controller)('tmf-api/alarmManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], AlarmProxyController);
//# sourceMappingURL=alarm-proxy.controller.js.map