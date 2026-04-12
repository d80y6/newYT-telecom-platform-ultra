import { Controller, Get, Post, Patch, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF640 - Resource Order Management')
@Controller('tmf-api/resourceOrderingManagement/v4')
export class ResourceOrderProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get('resourceOrder')
  @ApiOperation({ summary: 'List resource orders' })
  @ApiQuery({ name: 'state', required: false })
  @ApiQuery({ name: 'orderType', required: false })
  @ApiQuery({ name: 'priority', required: false })
  @ApiQuery({ name: 'resourceType', required: false })
  @ApiQuery({ name: 'page', required: false })
  @ApiQuery({ name: 'size', required: false })
  async listResourceOrders(
    @Query('state') state?: string,
    @Query('orderType') orderType?: string,
    @Query('priority') priority?: string,
    @Query('resourceType') resourceType?: string,
    @Query('page') page?: string,
    @Query('size') size?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder`, {
        params: { state, orderType, priority, resourceType, page, size },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Post('resourceOrder')
  @ApiOperation({ summary: 'Create resource order' })
  @HttpCode(201)
  async createResourceOrder(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Get('resourceOrder/:id')
  @ApiOperation({ summary: 'Get resource order' })
  @ApiParam({ name: 'id', description: 'Resource Order UUID' })
  async getResourceOrder(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Patch('resourceOrder/:id')
  @ApiOperation({ summary: 'Update resource order' })
  async updateResourceOrder(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Post('resourceOrder/:id/stateChange')
  @ApiOperation({ summary: 'Change resource order state' })
  async changeState(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}/stateChange`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Post('resourceOrder/:id/acknowledge')
  @ApiOperation({ summary: 'Acknowledge resource order' })
  async acknowledge(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}/acknowledge`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post('resourceOrder/:id/complete')
  @ApiOperation({ summary: 'Complete resource order' })
  async complete(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceOrderingManagement/v4/resourceOrder/${id}/complete`),
    );
    return this.mapToTmfResponse(data);
  }

  private mapToTmfResponse(data: any) {
    return data;
  }
}