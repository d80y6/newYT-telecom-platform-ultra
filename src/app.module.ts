import { Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { AuthModule } from './auth/auth.module';
import { JwtRevocationGuard } from './auth/guards/jwt-revocation.guard';

@Module({
  imports: [
    AuthModule,
  ],
  providers: [
    {
      provide: APP_GUARD,
      useClass: JwtRevocationGuard,
    },
  ],
})
export class AppModule {}
