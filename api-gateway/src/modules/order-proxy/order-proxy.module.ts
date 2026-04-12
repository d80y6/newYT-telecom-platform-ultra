import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { OrderProxyController } from './order-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [OrderProxyController],
})
export class OrderProxyModule {}
