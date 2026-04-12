import { Controller, Get, Post, Param, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('Fraud Detection')
@Controller('tmf-api/fraudManagement/v5')
export class FraudProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get('alert')
  @ApiOperation({ summary: 'List fraud alerts' })
  async listAlerts() {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/fraudManagement/v5/alert`),
    );
    return data;
  }

  @Post('alert')
  @HttpCode(201)
  @ApiOperation({ summary: 'Create fraud alert' })
  async createAlert(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/fraudManagement/v5/alert`, body),
    );
    return data;
  }

  @Get('alert/:id')
  @ApiOperation({ summary: 'Get fraud alert' })
  @ApiParam({ name: 'id', description: 'Alert UUID' })
  async getAlert(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/fraudManagement/v5/alert/${id}`),
    );
    return data;
  }

  @Post('alert/:id/confirm')
  @ApiOperation({ summary: 'Confirm fraud alert' })
  async confirmAlert(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/fraudManagement/v5/alert/${id}/confirm`, body),
    );
    return data;
  }
}