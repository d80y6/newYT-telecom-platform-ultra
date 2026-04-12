"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AppModule = void 0;
const common_1 = require("@nestjs/common");
const axios_1 = require("@nestjs/axios");
const party_proxy_module_1 = require("./modules/party-proxy/party-proxy.module");
const catalog_proxy_module_1 = require("./modules/catalog-proxy/catalog-proxy.module");
const order_proxy_module_1 = require("./modules/order-proxy/order-proxy.module");
const charging_proxy_module_1 = require("./modules/charging-proxy/charging-proxy.module");
const inventory_proxy_module_1 = require("./modules/inventory-proxy/inventory-proxy.module");
const service_order_proxy_module_1 = require("./modules/service-order-proxy/service-order-proxy.module");
const trouble_ticket_proxy_module_1 = require("./modules/trouble-ticket-proxy/trouble-ticket-proxy.module");
const billing_proxy_module_1 = require("./modules/billing-proxy/billing-proxy.module");
const resource_proxy_module_1 = require("./modules/resource-proxy/resource-proxy.module");
const usage_proxy_module_1 = require("./modules/usage-proxy/usage-proxy.module");
const notification_proxy_module_1 = require("./modules/notification-proxy/notification-proxy.module");
const customer360_proxy_module_1 = require("./modules/customer360-proxy/customer360-proxy.module");
const resource_order_proxy_module_1 = require("./modules/resource-order-proxy/resource-order-proxy.module");
const performance_proxy_module_1 = require("./modules/performance-proxy/performance-proxy.module");
const provisioning_proxy_module_1 = require("./modules/provisioning-proxy/provisioning-proxy.module");
const fault_proxy_module_1 = require("./modules/fault-proxy/fault-proxy.module");
const fraud_proxy_module_1 = require("./modules/fraud-proxy/fraud-proxy.module");
const analytics_proxy_module_1 = require("./modules/analytics-proxy/analytics-proxy.module");
const portal_proxy_module_1 = require("./modules/portal-proxy/portal-proxy.module");
const resource_catalog_proxy_module_1 = require("./modules/resource-catalog-proxy/resource-catalog-proxy.module");
const alarm_proxy_module_1 = require("./modules/alarm-proxy/alarm-proxy.module");
const identity_proxy_module_1 = require("./modules/identity-proxy/identity-proxy.module");
const user_proxy_module_1 = require("./modules/user-proxy/user-proxy.module");
const appointment_proxy_module_1 = require("./modules/appointment-proxy/appointment-proxy.module");
const product_price_proxy_module_1 = require("./modules/product-price-proxy/product-price-proxy.module");
const price_plan_proxy_module_1 = require("./modules/price-plan-proxy/price-plan-proxy.module");
const sales_proxy_module_1 = require("./modules/sales-proxy/sales-proxy.module");
const quote_proxy_module_1 = require("./modules/quote-proxy/quote-proxy.module");
const cart_proxy_module_1 = require("./modules/cart-proxy/cart-proxy.module");
let AppModule = class AppModule {
};
exports.AppModule = AppModule;
exports.AppModule = AppModule = __decorate([
    (0, common_1.Module)({
        imports: [
            axios_1.HttpModule.register({
                timeout: 30000,
                maxRedirects: 5,
            }),
            party_proxy_module_1.PartyProxyModule,
            catalog_proxy_module_1.CatalogProxyModule,
            order_proxy_module_1.OrderProxyModule,
            charging_proxy_module_1.ChargingProxyModule,
            inventory_proxy_module_1.InventoryProxyModule,
            service_order_proxy_module_1.ServiceOrderProxyModule,
            trouble_ticket_proxy_module_1.TroubleTicketProxyModule,
            billing_proxy_module_1.BillingProxyModule,
            resource_proxy_module_1.ResourceProxyModule,
            usage_proxy_module_1.UsageProxyModule,
            notification_proxy_module_1.NotificationProxyModule,
            customer360_proxy_module_1.Customer360ProxyModule,
            resource_order_proxy_module_1.ResourceOrderProxyModule,
            performance_proxy_module_1.PerformanceProxyModule,
            provisioning_proxy_module_1.ProvisioningProxyModule,
            fault_proxy_module_1.FaultProxyModule,
            fraud_proxy_module_1.FraudProxyModule,
            analytics_proxy_module_1.AnalyticsProxyModule,
            portal_proxy_module_1.PortalProxyModule,
            resource_catalog_proxy_module_1.ResourceCatalogProxyModule,
            alarm_proxy_module_1.AlarmProxyModule,
            identity_proxy_module_1.IdentityProxyModule,
            user_proxy_module_1.UserProxyModule,
            appointment_proxy_module_1.AppointmentProxyModule,
            product_price_proxy_module_1.ProductPriceProxyModule,
            price_plan_proxy_module_1.PricePlanProxyModule,
            sales_proxy_module_1.SalesProxyModule,
            quote_proxy_module_1.QuoteProxyModule,
            cart_proxy_module_1.CartProxyModule,
        ],
    })
], AppModule);
//# sourceMappingURL=app.module.js.map