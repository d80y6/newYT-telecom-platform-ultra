import { Controller, Get, Post, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF672 - Performance Management')
@Controller('tmf-api/performanceManagement/v5')
export class PerformanceProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get('performanceMetric')
  @ApiOperation({ summary: 'List performance metrics' })
  @ApiQuery({ name: 'resourceId', required: false })
  @ApiQuery({ name: 'severity', required: false })
  @ApiQuery({ name: 'page', required: false })
  @ApiQuery({ name: 'size', required: false })
  async listMetrics(
    @Query('resourceId') resourceId?: string,
    @Query('severity') severity?: string,
    @Query('page') page?: string,
    @Query('size') size?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/performanceManagement/v5/performanceMetric`, {
        params: { resourceId, severity, page, size },
      }),
    );
    return data;
  }

  @Post('performanceMetric')
  @ApiOperation({ summary: 'Create performance metric' })
  @HttpCode(201)
  async createMetric(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/performanceManagement/v5/performanceMetric`, body),
    );
    return data;
  }

  @Get('performanceMetric/:id')
  @ApiOperation({ summary: 'Get performance metric' })
  @ApiParam({ name: 'id', description: 'Metric UUID' })
  async getMetric(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/performanceManagement/v5/performanceMetric/${id}`),
    );
    return data;
  }

  @Get('performanceReport')
  @ApiOperation({ summary: 'Generate performance report' })
  @ApiQuery({ name: 'period', required: false })
  @ApiQuery({ name: 'resourceId', required: false })
  async generateReport(
    @Query('period') period?: string,
    @Query('resourceId') resourceId?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/performanceManagement/v5/performanceReport`, {
        params: { period, resourceId },
      }),
    );
    return data;
  }
}