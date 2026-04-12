import { Controller, Get, Post, Param, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF666 - Billing Account')
@Controller('tmf-api/customerBillManagement/v5')
export class BillingProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  async listBills() {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/customerBillManagement/v5/bill`));
    return data;
  }

  @Get(':id')
  async getBill(@Param('id') id: string) {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/customerBillManagement/v5/bill/${id}`));
    return data;
  }

  @Post()
  @HttpCode(201)
  async createBill(@Body() body: any) {
    const { data } = await firstValueFrom(this.httpService.post(`${BSS_CORE}/tmf-api/customerBillManagement/v5/bill`, body));
    return data;
  }
}