import { Controller, Get, Post, Param, Query, Body, HttpCode, Patch } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const CART_SERVICE = process.env.CART_SERVICE_URL || 'http://bss-core:8080/api/v1';

@ApiTags('TMF695 - Shopping Cart')
@Controller('tmf-api/shoppingCart/v5')
export class CartProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get('/cart')
  @ApiOperation({ summary: 'List shopping carts' })
  async listCarts(
    @Query('offset') offset?: string,
    @Query('limit') limit?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${CART_SERVICE}/cart`, {
        params: { offset, limit },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Get('/cart/:id')
  @ApiOperation({ summary: 'Retrieve a shopping cart' })
  @ApiParam({ name: 'id', description: 'Cart UUID' })
  async getCart(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${CART_SERVICE}/cart/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post('/cart')
  @ApiOperation({ summary: 'Create a shopping cart' })
  @HttpCode(201)
  async createCart(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${CART_SERVICE}/cart`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Patch('/cart/:id/status')
  @ApiOperation({ summary: 'Update cart status' })
  async updateCartStatus(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${CART_SERVICE}/cart/${id}/status`, body),
    );
    return this.mapToTmfResponse(data);
  }

  private mapToTmfResponse(data: any) {
    return {
      '@type': 'ShoppingCart',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }
}
