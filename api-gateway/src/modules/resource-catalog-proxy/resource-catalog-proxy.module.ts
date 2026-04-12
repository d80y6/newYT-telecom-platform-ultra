import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { ResourceCatalogProxyController } from './resource-catalog-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [ResourceCatalogProxyController],
})
export class ResourceCatalogProxyModule {}
