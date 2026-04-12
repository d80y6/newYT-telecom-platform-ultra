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
exports.TroubleTicketProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const BSS_CORE = process.env.BSS_CORE_URL || 'http://bss-core:8080';
let TroubleTicketProxyController = class TroubleTicketProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listTickets() {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE}/tmf-api/troubleTicketManagement/v5/troubleTicket`));
        return data;
    }
    async getTicket(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${BSS_CORE}/tmf-api/troubleTicketManagement/v5/troubleTicket/${id}`));
        return data;
    }
    async createTicket(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${BSS_CORE}/tmf-api/troubleTicketManagement/v5/troubleTicket`, body));
        return data;
    }
};
exports.TroubleTicketProxyController = TroubleTicketProxyController;
__decorate([
    (0, common_1.Get)(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], TroubleTicketProxyController.prototype, "listTickets", null);
__decorate([
    (0, common_1.Get)(':id'),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], TroubleTicketProxyController.prototype, "getTicket", null);
__decorate([
    (0, common_1.Post)(),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], TroubleTicketProxyController.prototype, "createTicket", null);
exports.TroubleTicketProxyController = TroubleTicketProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF642 - Trouble Ticket'),
    (0, common_1.Controller)('tmf-api/troubleTicketManagement/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], TroubleTicketProxyController);
//# sourceMappingURL=trouble-ticket-proxy.controller.js.map