import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { ProductPriceProxyController } from './product-price-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [ProductPriceProxyController],
})
export class ProductPriceProxyModule {}
