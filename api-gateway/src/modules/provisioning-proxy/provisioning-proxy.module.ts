import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { ProvisioningProxyController } from './provisioning-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [ProvisioningProxyController],
})
export class ProvisioningProxyModule {}