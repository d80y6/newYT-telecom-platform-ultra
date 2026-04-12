import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { AppointmentProxyController } from './appointment-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [AppointmentProxyController],
})
export class AppointmentProxyModule {}
