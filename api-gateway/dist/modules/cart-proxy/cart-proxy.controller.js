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
exports.CartProxyController = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const swagger_1 = require("@nestjs/swagger");
const rxjs_1 = require("rxjs");
const CART_SERVICE = process.env.CART_SERVICE_URL || 'http://bss-core:8080/api/v1';
let CartProxyController = class CartProxyController {
    constructor(httpService) {
        this.httpService = httpService;
    }
    async listCarts(offset, limit) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${CART_SERVICE}/cart`, {
            params: { offset, limit },
        }));
        return this.mapToTmfResponse(data);
    }
    async getCart(id) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.get(`${CART_SERVICE}/cart/${id}`));
        return this.mapToTmfResponse(data);
    }
    async createCart(body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.post(`${CART_SERVICE}/cart`, body));
        return this.mapToTmfResponse(data);
    }
    async updateCartStatus(id, body) {
        const { data } = await (0, rxjs_1.firstValueFrom)(this.httpService.patch(`${CART_SERVICE}/cart/${id}/status`, body));
        return this.mapToTmfResponse(data);
    }
    mapToTmfResponse(data) {
        return {
            '@type': 'ShoppingCart',
            '@baseType': 'BaseEntity',
            ...data,
        };
    }
};
exports.CartProxyController = CartProxyController;
__decorate([
    (0, common_1.Get)('/cart'),
    (0, swagger_1.ApiOperation)({ summary: 'List shopping carts' }),
    __param(0, (0, common_1.Query)('offset')),
    __param(1, (0, common_1.Query)('limit')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String]),
    __metadata("design:returntype", Promise)
], CartProxyController.prototype, "listCarts", null);
__decorate([
    (0, common_1.Get)('/cart/:id'),
    (0, swagger_1.ApiOperation)({ summary: 'Retrieve a shopping cart' }),
    (0, swagger_1.ApiParam)({ name: 'id', description: 'Cart UUID' }),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], CartProxyController.prototype, "getCart", null);
__decorate([
    (0, common_1.Post)('/cart'),
    (0, swagger_1.ApiOperation)({ summary: 'Create a shopping cart' }),
    (0, common_1.HttpCode)(201),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], CartProxyController.prototype, "createCart", null);
__decorate([
    (0, common_1.Patch)('/cart/:id/status'),
    (0, swagger_1.ApiOperation)({ summary: 'Update cart status' }),
    __param(0, (0, common_1.Param)('id')),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, Object]),
    __metadata("design:returntype", Promise)
], CartProxyController.prototype, "updateCartStatus", null);
exports.CartProxyController = CartProxyController = __decorate([
    (0, swagger_1.ApiTags)('TMF695 - Shopping Cart'),
    (0, common_1.Controller)('tmf-api/shoppingCart/v5'),
    __metadata("design:paramtypes", [axios_1.HttpService])
], CartProxyController);
//# sourceMappingURL=cart-proxy.controller.js.map