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
exports.AppointmentProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let AppointmentProxyController = class AppointmentProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async createAppointment(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment`, body));
        return data;
    }
    async listAppointments(page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment`, { params: { page, size } }));
        return data;
    }
    async getAppointment(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}`));
        return data;
    }
    async updateAppointment(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.put(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}`, body));
        return data;
    }
    async deleteAppointment(id) {
        await (0, rxjs_1.firstValueFrom)(this.httpService.delete(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}`));
        return { deleted: true };
    }
    async startAppointment(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}/start`, {}));
        return data;
    }
    async completeAppointment(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}/complete`, {}));
        return data;
    }
    async cancelAppointment(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}/cancel`, {}));
        return data;
    }
    async getAppointmentsByCustomer(customerId) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/customer/${customerId}`));
        return data;
    }
    async getAppointmentsByTechnician(technicianId) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/technician/${technicianId}`));
        return data;
    }
};
exports.AppointmentProxyController = AppointmentProxyController;
__decorate([
    (0, common_1.Post)('appointment'),
    (0, swagger_1.ApiOperation)({ summary: 'Create appointment' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "createAppointment", null);
__decorate([
    (0, common_1.Get)('appointment'),
    (0, swagger_1.ApiOperation)({ summary: 'List appointments' }),
    __param(0, (0, common_1.Query)('page')),
    __param(1, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "listAppointments", null);
__decorate([
    (0, common_1.Get)('appointment/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get appointment' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "getAppointment", null);
__decorate([
    (0, common_1.Put)('appointment/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update appointment' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "updateAppointment", null);
__decorate([
    (0, common_1.Delete)('appointment/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Delete appointment' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "deleteAppointment", null);
__decorate([
    (0, common_1.Patch)('appointment/:id/start'),
    (0, swagger_1.ApiOperation)({ summary: 'Start appointment' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "startAppointment", null);
__decorate([
    (0, common_1.Patch)('appointment/:id/complete'),
    (0, swagger_1.ApiOperation)({ summary: 'Complete appointment' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "completeAppointment", null);
__decorate([
    (0, common_1.Patch)('appointment/:id/cancel'),
    (0, swagger_1.ApiOperation)({ summary: 'Cancel appointment' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "cancelAppointment", null);
__decorate([
    (0, common_1.Get)('appointment/customer/:customerId'),
    (0, swagger_1.ApiOperation)({ summary: 'Get appointments by customer' }),
    __param(0, (0, common_1.Param)('customerId')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "getAppointmentsByCustomer", null);
__decorate([
    (0, common_1.Get)('appointment/technician/:technicianId'),
    (0, swagger_1.ApiOperation)({ summary: 'Get appointments by technician' }),
    __param(0, (0, common_1.Param)('technicianId')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], AppointmentProxyController.prototype, "getAppointmentsByTechnician", null);
exports.AppointmentProxyController = AppointmentProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF646 - Appointment Management'),
    (0, common_1.Controller)('tmf-api/appointmentManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], AppointmentProxyController);
//# sourceMappingURL=appointment-proxy.controller.js.map