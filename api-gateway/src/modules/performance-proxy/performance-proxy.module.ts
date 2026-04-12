import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { PerformanceProxyController } from './performance-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [PerformanceProxyController],
})
export class PerformanceProxyModule {}