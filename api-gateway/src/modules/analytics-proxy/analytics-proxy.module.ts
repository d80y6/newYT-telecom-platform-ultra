import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { AnalyticsProxyController } from './analytics-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [AnalyticsProxyController],
})
export class AnalyticsProxyModule {}