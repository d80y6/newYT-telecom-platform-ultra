import { Module } from '@nestjs/common';
import { ResourceProxyController } from './resource-proxy.controller';

@Module({ controllers: [ResourceProxyController] })
export class ResourceProxyModule {}