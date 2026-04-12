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
exports.PartyProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const PARTY_SERVICE = process.env.PARTY_SERVICE_URL || 'http://bss-core:8080/api/v1';
let PartyProxyController = class PartyProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listParties(offset, limit) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${PARTY_SERVICE}/customers`, {
            params: { offset, limit },
        }));
        return this.mapToTmfResponse(data);
    }
    async getParty(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${PARTY_SERVICE}/customers/${id}`));
        return this.mapToTmfResponse(data);
    }
    async createParty(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${PARTY_SERVICE}/customers`, body));
        return this.mapToTmfResponse(data);
    }
    async updateParty(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.put(`${PARTY_SERVICE}/customers/${id}`, body));
        return this.mapToTmfResponse(data);
    }
    async deleteParty(id) {
        await (0, rxjs_1.firstValueFrom)(this.httpService.delete(`${PARTY_SERVICE}/customers/${id}`));
    }
    mapToTmfResponse(data) {
        if (data.items) {
            return {
                '@type': 'Party',
                '@baseType': 'BaseEntity',
                ...data,
            };
        }
        return {
            '@type': 'Party',
            '@baseType': 'BaseEntity',
            ...data,
        };
    }
};
exports.PartyProxyController = PartyProxyController;
__decorate([
    (0, common_1.Get)(),
    (0, swagger_1.ApiOperation)({ summary: 'List all parties' }),
    (0, swagger_1.ApiQuery)({ name: 'offset', required: false }),
    (0, swagger_1.ApiQuery)({ name: 'limit', required: false }),
    __param(0, (0, common_1.Query)('offset')),
    __param(1, (0, common_1.Query)('limit')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], PartyProxyController.prototype, "listParties", null);
__decorate([
    (0, common_1.Get)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Retrieve a party by ID' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Party UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], PartyProxyController.prototype, "getParty", null);
__decorate([
    (0, common_1.Post)(),
    (0, swagger_1.ApiOperation)({ summary: 'Create a new party' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], PartyProxyController.prototype, "createParty", null);
__decorate([
    (0, common_1.Put)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update a party' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Party UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], PartyProxyController.prototype, "updateParty", null);
__decorate([
    (0, common_1.Delete)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Delete a party' }),
    (0, common_1.HttpCode)(204),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], PartyProxyController.prototype, "deleteParty", null);
exports.PartyProxyController = PartyProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF632 - Party Management'),
    (0, common_1.Controller)('tmf-api/partyManagement/v5/party'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], PartyProxyController);
//# sourceMappingURL=party-proxy.controller.js.map