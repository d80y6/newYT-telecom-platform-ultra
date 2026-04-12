import { Controller, Get, Post, Put, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const PRICE_SERVICE = process.env.PRICE_SERVICE_URL || 'http://bss-core:8080/api/v1';

@ApiTags('TMF655 - Price Recurring')
@Controller('tmf-api/productCatalogManagement/v5/pricePlan')
export class PricePlanProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  @ApiOperation({ summary: 'List price plans (recurring)' })
  async listPricePlans(
    @Query('offset') offset?: string,
    @Query('limit') limit?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${PRICE_SERVICE}/price-plan`, {
        params: { offset, limit },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Retrieve a price plan' })
  @ApiParam({ name: 'id', description: 'Plan UUID' })
  async getPricePlan(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${PRICE_SERVICE}/price-plan/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post()
  @ApiOperation({ summary: 'Create a price plan' })
  @HttpCode(201)
  async createPricePlan(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${PRICE_SERVICE}/price-plan`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update a price plan' })
  async updatePricePlan(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.put(`${PRICE_SERVICE}/price-plan/${id}`, body),
    );
    return this.mapToTmfResponse(data);
  }

  private mapToTmfResponse(data: any) {
    return {
      '@type': 'PricePlan',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }
}
