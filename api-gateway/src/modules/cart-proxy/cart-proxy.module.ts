import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { CartProxyController } from './cart-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [CartProxyController],
})
export class CartProxyModule {}
