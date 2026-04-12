import { Controller, Get, Post, Param, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF642 - Trouble Ticket')
@Controller('tmf-api/troubleTicketManagement/v5')
export class TroubleTicketProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  async listTickets() {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/troubleTicketManagement/v5/troubleTicket`));
    return data;
  }

  @Get(':id')
  async getTicket(@Param('id') id: string) {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/troubleTicketManagement/v5/troubleTicket/${id}`));
    return data;
  }

  @Post()
  @HttpCode(201)
  async createTicket(@Body() body: any) {
    const { data } = await firstValueFrom(this.httpService.post(`${BSS_CORE}/tmf-api/troubleTicketManagement/v5/troubleTicket`, body));
    return data;
  }
}