import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { AlarmProxyController } from './alarm-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [AlarmProxyController],
})
export class AlarmProxyModule {}
