import { Controller, Get, Post, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('OSS - Network Provisioning')
@Controller('tmf-api/serviceProvisioningManagement/v5')
export class ProvisioningProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get('provisioningOrder')
  @ApiOperation({ summary: 'List provisioning orders' })
  @ApiQuery({ name: 'status', required: false })
  @ApiQuery({ name: 'page', required: false })
  @ApiQuery({ name: 'size', required: false })
  async listProvisioningOrders(
    @Query('status') status?: string,
    @Query('page') page?: string,
    @Query('size') size?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder`, {
        params: { status, page, size },
      }),
    );
    return data;
  }

  @Post('provisioningOrder')
  @ApiOperation({ summary: 'Create provisioning order' })
  @HttpCode(201)
  async createProvisioningOrder(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder`, body),
    );
    return data;
  }

  @Get('provisioningOrder/:id')
  @ApiOperation({ summary: 'Get provisioning order' })
  @ApiParam({ name: 'id', description: 'Order UUID' })
  async getProvisioningOrder(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder/${id}`),
    );
    return data;
  }

  @Post('provisioningOrder/:id/assign')
  @ApiOperation({ summary: 'Assign technician' })
  async assignTechnician(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder/${id}/assign`, body),
    );
    return data;
  }

  @Post('provisioningOrder/:id/start')
  @ApiOperation({ summary: 'Start work' })
  async startWork(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder/${id}/start`),
    );
    return data;
  }

  @Post('provisioningOrder/:id/complete')
  @ApiOperation({ summary: 'Complete order' })
  async completeOrder(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/serviceProvisioningManagement/v5/provisioningOrder/${id}/complete`, body),
    );
    return data;
  }
}