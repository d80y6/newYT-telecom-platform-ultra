import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { ResourceOrderProxyController } from './resource-order-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [ResourceOrderProxyController],
})
export class ResourceOrderProxyModule {}