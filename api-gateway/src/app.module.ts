import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { PartyProxyModule } from './modules/party-proxy/party-proxy.module';
import { CatalogProxyModule } from './modules/catalog-proxy/catalog-proxy.module';
import { OrderProxyModule } from './modules/order-proxy/order-proxy.module';
import { ChargingProxyModule } from './modules/charging-proxy/charging-proxy.module';
import { InventoryProxyModule } from './modules/inventory-proxy/inventory-proxy.module';
import { ServiceOrderProxyModule } from './modules/service-order-proxy/service-order-proxy.module';
import { TroubleTicketProxyModule } from './modules/trouble-ticket-proxy/trouble-ticket-proxy.module';
import { BillingProxyModule } from './modules/billing-proxy/billing-proxy.module';
import { ResourceProxyModule } from './modules/resource-proxy/resource-proxy.module';
import { UsageProxyModule } from './modules/usage-proxy/usage-proxy.module';
import { NotificationProxyModule } from './modules/notification-proxy/notification-proxy.module';
import { Customer360ProxyModule } from './modules/customer360-proxy/customer360-proxy.module';
import { ResourceOrderProxyModule } from './modules/resource-order-proxy/resource-order-proxy.module';
import { PerformanceProxyModule } from './modules/performance-proxy/performance-proxy.module';
import { ProvisioningProxyModule } from './modules/provisioning-proxy/provisioning-proxy.module';
import { FaultProxyModule } from './modules/fault-proxy/fault-proxy.module';
import { FraudProxyModule } from './modules/fraud-proxy/fraud-proxy.module';
import { AnalyticsProxyModule } from './modules/analytics-proxy/analytics-proxy.module';
import { PortalProxyModule } from './modules/portal-proxy/portal-proxy.module';
import { ResourceCatalogProxyModule } from './modules/resource-catalog-proxy/resource-catalog-proxy.module';
import { AlarmProxyModule } from './modules/alarm-proxy/alarm-proxy.module';
import { IdentityProxyModule } from './modules/identity-proxy/identity-proxy.module';
import { UserProxyModule } from './modules/user-proxy/user-proxy.module';
import { AppointmentProxyModule } from './modules/appointment-proxy/appointment-proxy.module';
import { ProductPriceProxyModule } from './modules/product-price-proxy/product-price-proxy.module';
import { PricePlanProxyModule } from './modules/price-plan-proxy/price-plan-proxy.module';
import { SalesProxyModule } from './modules/sales-proxy/sales-proxy.module';
import { QuoteProxyModule } from './modules/quote-proxy/quote-proxy.module';
import { CartProxyModule } from './modules/cart-proxy/cart-proxy.module';

@Module({
  imports: [
    HttpModule.register({
      timeout: 30000,
      maxRedirects: 5,
    }),
    PartyProxyModule,
    CatalogProxyModule,
    OrderProxyModule,
    ChargingProxyModule,
    InventoryProxyModule,
    ServiceOrderProxyModule,
    TroubleTicketProxyModule,
    BillingProxyModule,
    ResourceProxyModule,
    UsageProxyModule,
    NotificationProxyModule,
    Customer360ProxyModule,
    ResourceOrderProxyModule,
    PerformanceProxyModule,
    ProvisioningProxyModule,
    FaultProxyModule,
    FraudProxyModule,
    AnalyticsProxyModule,
    PortalProxyModule,
    ResourceCatalogProxyModule,
    AlarmProxyModule,
    IdentityProxyModule,
    UserProxyModule,
    AppointmentProxyModule,
    ProductPriceProxyModule,
    PricePlanProxyModule,
    SalesProxyModule,
    QuoteProxyModule,
    CartProxyModule,
  ],
})
export class AppModule {}
