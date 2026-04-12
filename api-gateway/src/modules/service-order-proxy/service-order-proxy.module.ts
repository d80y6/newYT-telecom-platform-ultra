import { Module } from '@nestjs/common';
import { ServiceOrderProxyController } from './service-order-proxy.controller';

@Module({ controllers: [ServiceOrderProxyController] })
export class ServiceOrderProxyModule {}