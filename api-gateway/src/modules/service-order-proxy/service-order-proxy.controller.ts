import { Controller, Get, Post, Param, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF641 - Service Ordering')
@Controller('tmf-api/serviceOrderingManagement/v4')
export class ServiceOrderProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  @ApiOperation({ summary: 'List service orders' })
  async listOrders() {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/serviceOrderingManagement/v4/serviceOrder`));
    return data;
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get service order by ID' })
  async getOrder(@Param('id') id: string) {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/serviceOrderingManagement/v4/serviceOrder/${id}`));
    return data;
  }

  @Post()
  @HttpCode(201)
  @ApiOperation({ summary: 'Create service order' })
  async createOrder(@Body() body: any) {
    const { data } = await firstValueFrom(this.httpService.post(`${BSS_CORE}/tmf-api/serviceOrderingManagement/v4/serviceOrder`, body));
    return data;
  }
}