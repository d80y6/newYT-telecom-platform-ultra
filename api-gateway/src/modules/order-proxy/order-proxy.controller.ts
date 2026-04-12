import { Controller, Get, Post, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const ORDER_SERVICE = process.env.ORDER_SERVICE_URL || 'http://bss-core:8080/api/v1';

@ApiTags('TMF622 - Product Ordering Management')
@Controller('tmf-api/productOrderingManagement/v5/productOrder')
export class OrderProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  @ApiOperation({ summary: 'List product orders' })
  async listOrders(
    @Query('offset') offset?: string,
    @Query('limit') limit?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${ORDER_SERVICE}/orders`, {
        params: { offset, limit },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Retrieve a product order' })
  @ApiParam({ name: 'id', description: 'Order UUID' })
  async getOrder(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${ORDER_SERVICE}/orders/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post()
  @ApiOperation({ summary: 'Create a product order' })
  @HttpCode(201)
  async createOrder(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${ORDER_SERVICE}/orders`, body),
    );
    return this.mapToTmfResponse(data);
  }

  private mapToTmfResponse(data: any) {
    return {
      '@type': 'ProductOrder',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }
}
