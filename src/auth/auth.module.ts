import { Module, Global } from '@nestjs/common';
import { RedisModule } from '@nestjs-modules/ioredis';
import { JwtRevocationGuard } from './guards/jwt-revocation.guard';
import { RedisSubscriberService } from './services/redis-subscriber.service';

@Global()
@Module({
  imports: [
    RedisModule.forRoot({
      type: 'single',
      url: process.env.REDIS_URL || 'redis://localhost:6379',
    }),
  ],
  providers: [
    JwtRevocationGuard,
    RedisSubscriberService,
  ],
  exports: [
    JwtRevocationGuard,
    RedisSubscriberService,
  ],
})
export class AuthModule {}
