import { Module } from '@nestjs/common';
import { InventoryProxyController } from './inventory-proxy.controller';

@Module({
  controllers: [InventoryProxyController],
})
export class InventoryProxyModule {}