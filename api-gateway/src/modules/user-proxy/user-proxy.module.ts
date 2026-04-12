import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { UserProxyController } from './user-proxy.controller';

@Module({
  imports: [HttpModule],
  controllers: [UserProxyController],
})
export class UserProxyModule {}
