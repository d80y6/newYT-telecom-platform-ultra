import { Controller, Get, Post, Param, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF678 - Notification')
@Controller('tmf-api/notificationListener/v5')
export class NotificationProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  async listNotifications() {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/notificationListener/v5/notification`));
    return data;
  }

  @Post()
  @HttpCode(201)
  async createNotification(@Body() body: any) {
    const { data } = await firstValueFrom(this.httpService.post(`${BSS_CORE}/tmf-api/notificationListener/v5/notification`, body));
    return data;
  }
}