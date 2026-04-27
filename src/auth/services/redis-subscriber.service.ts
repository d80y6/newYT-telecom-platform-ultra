import { Injectable, OnModuleInit, OnModuleDestroy, Logger } from '@nestjs/common';
import { Redis } from 'ioredis';
import { InjectRedis } from '@nestjs-modules/ioredis';

interface RevocationMessage {
  jti: string;
  exp: number;
}

@Injectable()
export class RedisSubscriberService implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(RedisSubscriberService.name);
  private subscriberClient: Redis;

  constructor(
    @InjectRedis() private readonly redis: Redis
  ) {
    this.subscriberClient = this.redis.duplicate();
  }

  onModuleInit(): void {
    this.subscribeToRevocations();
  }

  onModuleDestroy(): void {
    this.subscriberClient.disconnect();
  }

  private subscribeToRevocations(): void {
    this.subscriberClient.subscribe('keycloak:revocations', (err) => {
      if (err) {
        this.logger.error('Failed to subscribe to keycloak:revocations', err);
        return;
      }
      this.logger.log('Subscribed to keycloak:revocations channel');
    });

    this.subscriberClient.on('message', (channel, message) => {
      if (channel === 'keycloak:revocations') {
        this.handleRevocationMessage(message);
      }
    });
  }

  private async handleRevocationMessage(message: string): Promise<void> {
    try {
      const revocationData: RevocationMessage = JSON.parse(message);
      const { jti, exp } = revocationData;

      if (!jti || !exp) {
        this.logger.warn('Invalid revocation message received', { message });
        return;
      }

      const now = Math.floor(Date.now() / 1000);
      const ttlSeconds = exp - now;

      if (ttlSeconds <= 0) {
        this.logger.debug('Token already expired, skipping revocation', { jti });
        return;
      }

      await this.redis.sadd('revoked:tokens', jti);
      await this.redis.expire('revoked:tokens', ttlSeconds);
      
      this.logger.log(`Token revoked: ${jti}, TTL: ${ttlSeconds}s`);
    } catch (error) {
      this.logger.error('Failed to process revocation message', error);
    }
  }
}
