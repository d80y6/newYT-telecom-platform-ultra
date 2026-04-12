import { Controller, Get, Post, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('OSS - Fault Management')
@Controller('tmf-api/faultManagement/v5')
export class FaultProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get('fault')
  @ApiOperation({ summary: 'List faults' })
  @ApiQuery({ name: 'status', required: false })
  @ApiQuery({ name: 'severity', required: false })
  @ApiQuery({ name: 'page', required: false })
  @ApiQuery({ name: 'size', required: false })
  async listFaults(
    @Query('status') status?: string,
    @Query('severity') severity?: string,
    @Query('page') page?: string,
    @Query('size') size?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault`, {
        params: { status, severity, page, size },
      }),
    );
    return data;
  }

  @Post('fault')
  @ApiOperation({ summary: 'Create fault' })
  @HttpCode(201)
  async createFault(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault`, body),
    );
    return data;
  }

  @Get('fault/:id')
  @ApiOperation({ summary: 'Get fault' })
  @ApiParam({ name: 'id', description: 'Fault UUID' })
  async getFault(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault/${id}`),
    );
    return data;
  }

  @Post('fault/:id/acknowledge')
  @ApiOperation({ summary: 'Acknowledge fault' })
  async acknowledgeFault(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault/${id}/acknowledge`, body),
    );
    return data;
  }

  @Post('fault/:id/clear')
  @ApiOperation({ summary: 'Clear fault' })
  async clearFault(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault/${id}/clear`, body),
    );
    return data;
  }

  @Post('fault/:id/close')
  @ApiOperation({ summary: 'Close fault' })
  async closeFault(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/faultManagement/v5/fault/${id}/close`),
    );
    return data;
  }
}