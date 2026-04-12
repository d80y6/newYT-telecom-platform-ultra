import { Controller, Get, Post, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const ANALYTICS_SERVICE = process.env.ANALYTICS_SERVICE_URL || 'http://bss-core:8080/api/v1';

@ApiTags('TMFxxx - Analytics')
@Controller('tmf-api/analytics/v5')
export class AnalyticsProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get('/metric')
  @ApiOperation({ summary: 'List analytics metrics' })
  async listMetrics(
    @Query('category') category?: string,
    @Query('page') page?: string,
    @Query('size') size?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/metric`, {
        params: { category, page, size },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Get('/metric/:id')
  @ApiOperation({ summary: 'Retrieve an analytics metric' })
  @ApiParam({ name: 'id', description: 'Metric UUID' })
  async getMetric(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/metric/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post('/metric')
  @ApiOperation({ summary: 'Create an analytics metric' })
  @HttpCode(201)
  async createMetric(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${ANALYTICS_SERVICE}/analytics/v5/metric`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Get('/dashboard')
  @ApiOperation({ summary: 'Get dashboard summary' })
  async getDashboard() {
    const { data } = await firstValueFrom(
      this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/dashboard`),
    );
    return data;
  }

  @Get('/kpi')
  @ApiOperation({ summary: 'Get KPIs' })
  async getKpis() {
    const { data } = await firstValueFrom(
      this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/kpi`),
    );
    return data;
  }

  @Get('/timeSeries')
  @ApiOperation({ summary: 'Get time series data' })
  async getTimeSeries(
    @Query('metricName') metricName: string,
    @Query('startTime') startTime: string,
    @Query('endTime') endTime: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${ANALYTICS_SERVICE}/analytics/v5/timeSeries`, {
        params: { metricName, startTime, endTime },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  private mapToTmfResponse(data: any) {
    return {
      '@type': 'AnalyticsMetric',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }
}
