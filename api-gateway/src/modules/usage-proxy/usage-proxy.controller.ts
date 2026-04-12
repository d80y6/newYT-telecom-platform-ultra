import { Controller, Get, Post, Param, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF688 - Usage Management')
@Controller('tmf-api/usageManagement/v5')
export class UsageProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  async listUsage() {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/usageManagement/v5/usage`));
    return data;
  }

  @Get(':id')
  async getUsage(@Param('id') id: string) {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/usageManagement/v5/usage/${id}`));
    return data;
  }

  @Post()
  @HttpCode(201)
  async createUsage(@Body() body: any) {
    const { data } = await firstValueFrom(this.httpService.post(`${BSS_CORE}/tmf-api/usageManagement/v5/usage`, body));
    return data;
  }
}