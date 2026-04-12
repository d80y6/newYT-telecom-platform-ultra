import { Controller, Get, Post, Put, Patch, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF654 - Alarm Management')
@Controller('tmf-api/alarmManagement/v5')
export class AlarmProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Post('alarm')
  @ApiOperation({ summary: 'Create alarm' })
  @HttpCode(201)
  async createAlarm(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm`, body),
    );
    return data;
  }

  @Get('alarm')
  @ApiOperation({ summary: 'List alarms' })
  @ApiQuery({ name: 'page', required: false })
  @ApiQuery({ name: 'size', required: false })
  async listAlarms(
    @Query('page') page?: string,
    @Query('size') size?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm`, {
        params: { page, size },
      }),
    );
    return data;
  }

  @Get('alarm/:id')
  @ApiOperation({ summary: 'Get alarm' })
  @ApiParam({ name: 'id', description: 'Alarm UUID' })
  async getAlarm(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}`),
    );
    return data;
  }

  @Put('alarm/:id')
  @ApiOperation({ summary: 'Update alarm' })
  @ApiParam({ name: 'id', description: 'Alarm UUID' })
  async updateAlarm(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.put(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}`, body),
    );
    return data;
  }

  @Delete('alarm/:id')
  @ApiOperation({ summary: 'Delete alarm' })
  @ApiParam({ name: 'id', description: 'Alarm UUID' })
  async deleteAlarm(@Param('id') id: string) {
    await firstValueFrom(
      this.httpService.delete(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}`),
    );
    return { deleted: true };
  }

  @Patch('alarm/:id/acknowledge')
  @ApiOperation({ summary: 'Acknowledge alarm' })
  @ApiParam({ name: 'id', description: 'Alarm UUID' })
  async acknowledgeAlarm(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}/acknowledge`, {}),
    );
    return data;
  }

  @Patch('alarm/:id/clear')
  @ApiOperation({ summary: 'Clear alarm' })
  @ApiParam({ name: 'id', description: 'Alarm UUID' })
  async clearAlarm(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}/clear`, {}),
    );
    return data;
  }

  @Patch('alarm/:id/close')
  @ApiOperation({ summary: 'Close alarm' })
  @ApiParam({ name: 'id', description: 'Alarm UUID' })
  async closeAlarm(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/${id}/close`, {}),
    );
    return data;
  }

  @Get('alarm/status/:status')
  @ApiOperation({ summary: 'Get alarms by status' })
  @ApiParam({ name: 'status', description: 'Alarm status' })
  async getAlarmsByStatus(@Param('status') status: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/status/${status}`),
    );
    return data;
  }

  @Get('alarm/severity/:severity')
  @ApiOperation({ summary: 'Get alarms by severity' })
  @ApiParam({ name: 'severity', description: 'Alarm severity' })
  async getAlarmsBySeverity(@Param('severity') severity: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/severity/${severity}`),
    );
    return data;
  }

  @Get('alarm/resource/:resourceId')
  @ApiOperation({ summary: 'Get alarms for resource' })
  @ApiParam({ name: 'resourceId', description: 'Resource UUID' })
  async getAlarmsForResource(@Param('resourceId') resourceId: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/alarmManagement/v5/alarm/resource/${resourceId}`),
    );
    return data;
  }
}
