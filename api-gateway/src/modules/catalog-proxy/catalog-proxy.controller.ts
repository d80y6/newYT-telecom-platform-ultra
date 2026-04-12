import { Controller, Get, Post, Put, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const CATALOG_SERVICE = process.env.CATALOG_SERVICE_URL || 'http://bss-core:8080/api/v1';

@ApiTags('TMF620 - Product Catalog Management')
@Controller('tmf-api/productCatalogManagement/v5/productOffering')
export class CatalogProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  @ApiOperation({ summary: 'List product offerings' })
  async listOfferings(
    @Query('offset') offset?: string,
    @Query('limit') limit?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${CATALOG_SERVICE}/products/offerings`, {
        params: { offset, limit },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Retrieve a product offering' })
  @ApiParam({ name: 'id', description: 'Offering UUID' })
  async getOffering(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${CATALOG_SERVICE}/products/offerings/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post()
  @ApiOperation({ summary: 'Create a product offering' })
  @HttpCode(201)
  async createOffering(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${CATALOG_SERVICE}/products/offerings`, body),
    );
    return this.mapToTmfResponse(data);
  }

  private mapToTmfResponse(data: any) {
    return {
      '@type': 'ProductOffering',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }
}
