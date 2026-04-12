import { Controller, Get, Post, Put, Patch, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF646 - Appointment Management')
@Controller('tmf-api/appointmentManagement/v5')
export class AppointmentProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Post('appointment')
  @ApiOperation({ summary: 'Create appointment' })
  @HttpCode(201)
  async createAppointment(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment`, body),
    );
    return data;
  }

  @Get('appointment')
  @ApiOperation({ summary: 'List appointments' })
  async listAppointments(@Query('page') page?: string, @Query('size') size?: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment`, { params: { page, size } }),
    );
    return data;
  }

  @Get('appointment/:id')
  @ApiOperation({ summary: 'Get appointment' })
  async getAppointment(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}`),
    );
    return data;
  }

  @Put('appointment/:id')
  @ApiOperation({ summary: 'Update appointment' })
  async updateAppointment(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.put(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}`, body),
    );
    return data;
  }

  @Delete('appointment/:id')
  @ApiOperation({ summary: 'Delete appointment' })
  async deleteAppointment(@Param('id') id: string) {
    await firstValueFrom(this.httpService.delete(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}`));
    return { deleted: true };
  }

  @Patch('appointment/:id/start')
  @ApiOperation({ summary: 'Start appointment' })
  async startAppointment(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}/start`, {}),
    );
    return data;
  }

  @Patch('appointment/:id/complete')
  @ApiOperation({ summary: 'Complete appointment' })
  async completeAppointment(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}/complete`, {}),
    );
    return data;
  }

  @Patch('appointment/:id/cancel')
  @ApiOperation({ summary: 'Cancel appointment' })
  async cancelAppointment(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/${id}/cancel`, {}),
    );
    return data;
  }

  @Get('appointment/customer/:customerId')
  @ApiOperation({ summary: 'Get appointments by customer' })
  async getAppointmentsByCustomer(@Param('customerId') customerId: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/customer/${customerId}`),
    );
    return data;
  }

  @Get('appointment/technician/:technicianId')
  @ApiOperation({ summary: 'Get appointments by technician' })
  async getAppointmentsByTechnician(@Param('technicianId') technicianId: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/appointmentManagement/v5/appointment/technician/${technicianId}`),
    );
    return data;
  }
}
