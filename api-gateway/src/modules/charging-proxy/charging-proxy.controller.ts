import { Controller, Get, Post, Param, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const CHARGING_ENGINE = process.env.CHARGING_ENGINE_URL || 'http://charging-engine:8081';

@ApiTags('TMF654 - Prepay Balance Management')
@Controller('tmf-api/prepayBalanceManagement/v5')
export class ChargingProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get('account/:id/balance')
  @ApiOperation({ summary: 'Get account balance' })
  @ApiParam({ name: 'id', description: 'Account ID' })
  async getBalance(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${CHARGING_ENGINE}/api/v1/balance/${id}`),
    );
    return {
      '@type': 'PrepayBalance',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }

  @Post('account/:id/balance/reserve')
  @ApiOperation({ summary: 'Reserve balance for charging' })
  @HttpCode(200)
  async reserveBalance(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${CHARGING_ENGINE}/api/v1/balance/${id}/reserve`, body),
    );
    return data;
  }

  @Post('account/:id/balance/confirm')
  @ApiOperation({ summary: 'Confirm reserved balance deduction' })
  @HttpCode(200)
  async confirmDeduction(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${CHARGING_ENGINE}/api/v1/balance/${id}/confirm`, body),
    );
    return data;
  }
}
