import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { CatalogProxyController } from './catalog-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [CatalogProxyController],
})
export class CatalogProxyModule {}
