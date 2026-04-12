import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { QuoteProxyController } from './quote-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [QuoteProxyController],
})
export class QuoteProxyModule {}
