import { Controller, Get, Post, Patch, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF629 - Customer Management')
@Controller('tmf-api/customerManagement/v4')
export class Customer360ProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get('customer')
  @ApiOperation({ summary: 'List customers with 360 view' })
  @ApiQuery({ name: 'segment', required: false })
  @ApiQuery({ name: 'customerType', required: false })
  @ApiQuery({ name: 'status', required: false })
  @ApiQuery({ name: 'page', required: false })
  @ApiQuery({ name: 'size', required: false })
  async listCustomers360(
    @Query('segment') segment?: string,
    @Query('customerType') customerType?: string,
    @Query('status') status?: string,
    @Query('page') page?: string,
    @Query('size') size?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer`, {
        params: { segment, customerType, status, page, size },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Post('customer')
  @ApiOperation({ summary: 'Create customer with full profile' })
  @HttpCode(201)
  async createCustomer360(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Get('customer/:id')
  @ApiOperation({ summary: 'Get customer 360 view' })
  @ApiParam({ name: 'id', description: 'Customer UUID' })
  async getCustomer360(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Get('customer/:id/segment')
  @ApiOperation({ summary: 'Get customer segments' })
  @ApiParam({ name: 'id', description: 'Customer UUID' })
  async getCustomerSegments(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/segment`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post('customer/:id/segment')
  @ApiOperation({ summary: 'Assign segment to customer' })
  @ApiParam({ name: 'id', description: 'Customer UUID' })
  @HttpCode(201)
  async assignSegment(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/segment`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Get('customer/:id/hierarchy')
  @ApiOperation({ summary: 'Get account hierarchy' })
  @ApiParam({ name: 'id', description: 'Customer UUID' })
  async getAccountHierarchy(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/hierarchy`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post('customer/:id/hierarchy')
  @ApiOperation({ summary: 'Add account relationship' })
  @ApiParam({ name: 'id', description: 'Customer UUID' })
  @HttpCode(201)
  async addAccountRelationship(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/hierarchy`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Patch('customer/:id/engagement')
  @ApiOperation({ summary: 'Update engagement score' })
  async updateEngagement(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/customerManagement/v4/customer/${id}/engagement`, body),
    );
    return this.mapToTmfResponse(data);
  }

  private mapToTmfResponse(data: any) {
    return data;
  }
}