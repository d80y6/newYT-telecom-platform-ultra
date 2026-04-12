import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { SalesProxyController } from './sales-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [SalesProxyController],
})
export class SalesProxyModule {}
