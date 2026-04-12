import { Controller, Get, Post, Put, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF637 - Product Inventory')
@Controller('tmf-api/productInventory/v5')
export class InventoryProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  @ApiOperation({ summary: 'List product inventory' })
  async listInventory(@Query('offset') offset?: string, @Query('limit') limit?: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE}/tmf-api/productInventory/v5/productInventory`, { params: { offset, limit } }),
    );
    return data;
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get product inventory by ID' })
  async getInventory(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE}/tmf-api/productInventory/v5/productInventory/${id}`),
    );
    return data;
  }

  @Post()
  @HttpCode(201)
  @ApiOperation({ summary: 'Create product inventory' })
  async createInventory(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE}/tmf-api/productInventory/v5/productInventory`, body),
    );
    return data;
  }
}