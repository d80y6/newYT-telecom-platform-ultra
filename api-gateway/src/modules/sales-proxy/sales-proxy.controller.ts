import { Controller, Get, Post, Put, Param, Query, Body, HttpCode, Patch } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const SALES_SERVICE = process.env.SALES_SERVICE_URL || 'http://bss-core:8080/api/v1';

@ApiTags('TMF699 - Sales Lead')
@Controller('tmf-api/salesLeadManagement/v5')
export class SalesProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  @ApiOperation({ summary: 'List sales leads' })
  async listLeads(
    @Query('offset') offset?: string,
    @Query('limit') limit?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${SALES_SERVICE}/sales-lead`, {
        params: { offset, limit },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Retrieve a sales lead' })
  @ApiParam({ name: 'id', description: 'Lead UUID' })
  async getLead(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${SALES_SERVICE}/sales-lead/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post()
  @ApiOperation({ summary: 'Create a sales lead' })
  @HttpCode(201)
  async createLead(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${SALES_SERVICE}/sales-lead`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Patch(':id')
  @ApiOperation({ summary: 'Update sales lead status' })
  async updateLeadStatus(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${SALES_SERVICE}/sales-lead/${id}/status`, body),
    );
    return this.mapToTmfResponse(data);
  }

  private mapToTmfResponse(data: any) {
    return {
      '@type': 'SalesLead',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }
}
