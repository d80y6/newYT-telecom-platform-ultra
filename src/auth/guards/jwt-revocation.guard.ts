import { Injectable, CanActivate, ExecutionContext, UnauthorizedException } from '@nestjs/common';
import { Observable } from 'rxjs';
import { Redis } from 'ioredis';
import { InjectRedis } from '@nestjs-modules/ioredis';

@Injectable()
export class JwtRevocationGuard implements CanActivate {
  constructor(
    @InjectRedis() private readonly redis: Redis
  ) {}

  canActivate(
    context: ExecutionContext,
  ): boolean | Promise<boolean> | Observable<boolean> {
    const request = context.switchToHttp().getRequest();
    const token = this.extractTokenFromHeader(request);
    
    if (!token) {
      throw new UnauthorizedException('No token provided');
    }

    return this.checkTokenRevocation(token);
  }

  private async checkTokenRevocation(token: string): Promise<boolean> {
    try {
      const payload = this.decodeTokenPayload(token);
      const jti = payload?.jti;
      
      if (!jti) {
        throw new UnauthorizedException('Invalid token: missing JTI');
      }

      const isRevoked = await this.redis.sismember('revoked:tokens', jti);
      
      if (isRevoked) {
        throw new UnauthorizedException('Token has been revoked');
      }

      return true;
    } catch (error) {
      if (error instanceof UnauthorizedException) {
        throw error;
      }
      throw new UnauthorizedException('Token validation failed');
    }
  }

  private extractTokenFromHeader(request: any): string | undefined {
    const [type, token] = request.headers.authorization?.split(' ') ?? [];
    return type === 'Bearer' ? token : undefined;
  }

  private decodeTokenPayload(token: string): any {
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('Invalid token format');
    }
    const payload = Buffer.from(parts[1], 'base64').toString('utf-8');
    return JSON.parse(payload);
  }
}
