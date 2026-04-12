import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { PartyProxyController } from './party-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [PartyProxyController],
})
export class PartyProxyModule {}
