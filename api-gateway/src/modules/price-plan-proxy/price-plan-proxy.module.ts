import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { PricePlanProxyController } from './price-plan-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [PricePlanProxyController],
})
export class PricePlanProxyModule {}
