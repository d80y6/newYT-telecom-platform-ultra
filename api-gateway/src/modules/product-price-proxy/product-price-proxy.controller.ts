import { Controller, Get, Post, Put, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const PRICE_SERVICE = process.env.PRICE_SERVICE_URL || 'http://bss-core:8080/api/v1';

@ApiTags('TMF625 - Product Pricing')
@Controller('tmf-api/productCatalogManagement/v5/productPrice')
export class ProductPriceProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  @ApiOperation({ summary: 'List product prices' })
  async listPrices(
    @Query('offset') offset?: string,
    @Query('limit') limit?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${PRICE_SERVICE}/product-price`, {
        params: { offset, limit },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Retrieve a product price' })
  @ApiParam({ name: 'id', description: 'Price UUID' })
  async getPrice(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${PRICE_SERVICE}/product-price/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post()
  @ApiOperation({ summary: 'Create a product price' })
  @HttpCode(201)
  async createPrice(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${PRICE_SERVICE}/product-price`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update a product price' })
  async updatePrice(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.put(`${PRICE_SERVICE}/product-price/${id}`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Delete(':id')
  @ApiOperation({ summary: 'Delete a product price' })
  @HttpCode(204)
  async deletePrice(@Param('id') id: string) {
    await firstValueFrom(this.httpService.delete(`${PRICE_SERVICE}/product-price/${id}`));
  }

  private mapToTmfResponse(data: any) {
    return {
      '@type': 'ProductPrice',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }
}
