import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { FraudProxyController } from './fraud-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [FraudProxyController],
})
export class FraudProxyModule {}