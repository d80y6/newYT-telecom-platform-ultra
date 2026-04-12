import { Module } from '@nestjs/common';
import { UsageProxyController } from './usage-proxy.controller';

@Module({ controllers: [UsageProxyController] })
export class UsageProxyModule {}