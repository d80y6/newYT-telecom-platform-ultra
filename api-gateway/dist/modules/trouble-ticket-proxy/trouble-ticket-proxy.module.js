"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.TroubleTicketProxyModule = void 0;
const common_1 = require("@nestjs/common");
const trouble_ticket_proxy_controller_1 = require("./trouble-ticket-proxy.controller");
let TroubleTicketProxyModule = class TroubleTicketProxyModule {
};
exports.TroubleTicketProxyModule = TroubleTicketProxyModule;
exports.TroubleTicketProxyModule = TroubleTicketProxyModule = __decorate([
    (0, common_1.Module)({ controllers: [trouble_ticket_proxy_controller_1.TroubleTicketProxyController] })
], TroubleTicketProxyModule);
//# sourceMappingURL=trouble-ticket-proxy.module.js.map