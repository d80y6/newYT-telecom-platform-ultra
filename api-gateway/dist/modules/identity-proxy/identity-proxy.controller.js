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
exports.IdentityProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let IdentityProxyController = class IdentityProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async createIdentity(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity`, body));
        return data;
    }
    async listIdentities(page, size) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity`, { params: { page, size } }));
        return data;
    }
    async getIdentity(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}`));
        return data;
    }
    async updateIdentity(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.put(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}`, body));
        return data;
    }
    async deleteIdentity(id) {
        await (0, rxjs_1.firstValueFrom)(this.httpService.delete(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}`));
        return { deleted: true };
    }
    async verifyIdentity(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}/verify`, {}));
        return data;
    }
    async setPrimary(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}/primary`, {}));
        return data;
    }
    async revokeIdentity(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}/revoke`, {}));
        return data;
    }
    async getIdentitiesByParty(partyId) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/party/${partyId}`));
        return data;
    }
};
exports.IdentityProxyController = IdentityProxyController;
__decorate([
    (0, common_1.Post)('identity'),
    (0, swagger_1.ApiOperation)({ summary: 'Create identity' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], IdentityProxyController.prototype, "createIdentity", null);
__decorate([
    (0, common_1.Get)('identity'),
    (0, swagger_1.ApiOperation)({ summary: 'List identities' }),
    (0, swagger_1.ApiQuery)({ name: 'page', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'size', required: false }),
    __param(0, (0, common_1.Query)('page')),
    __param(1, (0, common_1.Query)('size')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], IdentityProxyController.prototype, "listIdentities", null);
__decorate([
    (0, common_1.Get)('identity/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get identity' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Identity UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], IdentityProxyController.prototype, "getIdentity", null);
__decorate([
    (0, common_1.Put)('identity/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update identity' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Identity UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], IdentityProxyController.prototype, "updateIdentity", null);
__decorate([
    (0, common_1.Delete)('identity/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Delete identity' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Identity UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], IdentityProxyController.prototype, "deleteIdentity", null);
__decorate([
    (0, common_1.Patch)('identity/:id/verify'),
    (0, swagger_1.ApiOperation)({ summary: 'Verify identity' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], IdentityProxyController.prototype, "verifyIdentity", null);
__decorate([
    (0, common_1.Patch)('identity/:id/primary'),
    (0, swagger_1.ApiOperation)({ summary: 'Set as primary' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], IdentityProxyController.prototype, "setPrimary", null);
__decorate([
    (0, common_1.Patch)('identity/:id/revoke'),
    (0, swagger_1.ApiOperation)({ summary: 'Revoke identity' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], IdentityProxyController.prototype, "revokeIdentity", null);
__decorate([
    (0, common_1.Get)('identity/party/:partyId'),
    (0, swagger_1.ApiOperation)({ summary: 'Get identities by party' }),
    __param(0, (0, common_1.Param)('partyId')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], IdentityProxyController.prototype, "getIdentitiesByParty", null);
exports.IdentityProxyController = IdentityProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF656 - Identity Management'),
    (0, common_1.Controller)('tmf-api/identityManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], IdentityProxyController);
//# sourceMappingURL=identity-proxy.controller.js.map