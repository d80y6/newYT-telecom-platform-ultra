import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { FaultProxyController } from './fault-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [FaultProxyController],
})
export class FaultProxyModule {}