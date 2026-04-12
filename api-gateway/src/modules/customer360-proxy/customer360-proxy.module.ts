import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { Customer360ProxyController } from './customer360-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [Customer360ProxyController],
})
export class Customer360ProxyModule {}