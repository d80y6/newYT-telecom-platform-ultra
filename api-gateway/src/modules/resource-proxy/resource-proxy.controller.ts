import { Controller, Get, Post, Param, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF638 - Resource Inventory')
@Controller('tmf-api/resourceInventoryManagement/v4')
export class ResourceProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  async listResources() {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/resourceInventoryManagement/v4/resource`));
    return data;
  }

  @Get(':id')
  async getResource(@Param('id') id: string) {
    const { data } = await firstValueFrom(this.httpService.get(`${BSS_CORE}/tmf-api/resourceInventoryManagement/v4/resource/${id}`));
    return data;
  }

  @Post()
  @HttpCode(201)
  async createResource(@Body() body: any) {
    const { data } = await firstValueFrom(this.httpService.post(`${BSS_CORE}/tmf-api/resourceInventoryManagement/v4/resource`, body));
    return data;
  }
}