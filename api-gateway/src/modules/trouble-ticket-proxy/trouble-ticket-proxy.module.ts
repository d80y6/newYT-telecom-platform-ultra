import { Module } from '@nestjs/common';
import { TroubleTicketProxyController } from './trouble-ticket-proxy.controller';

@Module({ controllers: [TroubleTicketProxyController] })
export class TroubleTicketProxyModule {}