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
exports.UserProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let UserProxyController = class UserProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async createUser(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user`, body));
        return data;
    }
    async listUsers(page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user`, { params: { page, size } }));
        return data;
    }
    async getUser(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}`));
        return data;
    }
    async updateUser(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.put(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}`, body));
        return data;
    }
    async deleteUser(id) {
        await (0, rxjs_1.firstValueFrom)(this.httpService.delete(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}`));
        return { deleted: true };
    }
    async suspendUser(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}/suspend`, {}));
        return data;
    }
    async activateUser(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}/activate`, {}));
        return data;
    }
};
exports.UserProxyController = UserProxyController;
__decorate([
    (0, common_1.Post)('user'),
    (0, swagger_1.ApiOperation)({ summary: 'Create user' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], UserProxyController.prototype, "createUser", null);
__decorate([
    (0, common_1.Get)('user'),
    (0, swagger_1.ApiOperation)({ summary: 'List users' }),
    __param(0, (0, common_1.Query)('page')),
    __param(1, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], UserProxyController.prototype, "listUsers", null);
__decorate([
    (0, common_1.Get)('user/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get user' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], UserProxyController.prototype, "getUser", null);
__decorate([
    (0, common_1.Put)('user/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update user' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], UserProxyController.prototype, "updateUser", null);
__decorate([
    (0, common_1.Delete)('user/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Delete user' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], UserProxyController.prototype, "deleteUser", null);
__decorate([
    (0, common_1.Patch)('user/:id/suspend'),
    (0, swagger_1.ApiOperation)({ summary: 'Suspend user' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], UserProxyController.prototype, "suspendUser", null);
__decorate([
    (0, common_1.Patch)('user/:id/activate'),
    (0, swagger_1.ApiOperation)({ summary: 'Activate user' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], UserProxyController.prototype, "activateUser", null);
exports.UserProxyController = UserProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF653 - User Management'),
    (0, common_1.Controller)('tmf-api/userManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], UserProxyController);
//# sourceMappingURL=user-proxy.controller.js.map