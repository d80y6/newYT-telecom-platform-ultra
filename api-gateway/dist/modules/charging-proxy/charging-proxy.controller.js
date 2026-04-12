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
exports.ChargingProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const CHARGING_ENGINE = process.env.CHARGING_ENGINE_URL || 'http://charging-engine:8081';
let ChargingProxyController = class ChargingProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async getBalance(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${CHARGING_ENGINE}/api/v1/balance/${id}`));
        return {
            '@type': 'PrepayBalance',
            '@baseType': 'BaseEntity',
            ...data,
        };
    }
    async reserveBalance(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${CHARGING_ENGINE}/api/v1/balance/${id}/reserve`, body));
        return data;
    }
    async confirmDeduction(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${CHARGING_ENGINE}/api/v1/balance/${id}/confirm`, body));
        return data;
    }
};
exports.ChargingProxyController = ChargingProxyController;
__decorate([
    (0, common_1.Get)('account/:id/balance'),
    (0, swagger_1.ApiOperation)({ summary: 'Get account balance' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Account ID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], ChargingProxyController.prototype, "getBalance", null);
__decorate([
    (0, common_1.Post)('account/:id/balance/reserve'),
    (0, swagger_1.ApiOperation)({ summary: 'Reserve balance for charging' }),
    (0, common_1.HttpCode)(200),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], ChargingProxyController.prototype, "reserveBalance", null);
__decorate([
    (0, common_1.Post)('account/:id/balance/confirm'),
    (0, swagger_1.ApiOperation)({ summary: 'Confirm reserved balance deduction' }),
    (0, common_1.HttpCode)(200),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], ChargingProxyController.prototype, "confirmDeduction", null);
exports.ChargingProxyController = ChargingProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF654 - Prepay Balance Management'),
    (0, common_1.Controller)('tmf-api/prepayBalanceManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], ChargingProxyController);
//# sourceMappingURL=charging-proxy.controller.js.map