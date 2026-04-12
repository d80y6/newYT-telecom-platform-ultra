import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { ChargingProxyController } from './charging-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [ChargingProxyController],
})
export class ChargingProxyModule {}
