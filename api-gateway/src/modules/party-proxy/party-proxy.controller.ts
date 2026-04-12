import { Controller, Get, Post, Put, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const PARTY_SERVICE = process.env.PARTY_SERVICE_URL || 'http://bss-core:8080/api/v1';

@ApiTags('TMF632 - Party Management')
@Controller('tmf-api/partyManagement/v5/party')
export class PartyProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  @ApiOperation({ summary: 'List all parties' })
  @ApiQuery({ name: 'offset', required: false })
  @ApiQuery({ name: 'limit', required: false })
  async listParties(
    @Query('offset') offset?: string,
    @Query('limit') limit?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${PARTY_SERVICE}/customers`, {
        params: { offset, limit },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Retrieve a party by ID' })
  @ApiParam({ name: 'id', description: 'Party UUID' })
  async getParty(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${PARTY_SERVICE}/customers/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post()
  @ApiOperation({ summary: 'Create a new party' })
  @HttpCode(201)
  async createParty(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${PARTY_SERVICE}/customers`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update a party' })
  @ApiParam({ name: 'id', description: 'Party UUID' })
  async updateParty(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.put(`${PARTY_SERVICE}/customers/${id}`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Delete(':id')
  @ApiOperation({ summary: 'Delete a party' })
  @HttpCode(204)
  async deleteParty(@Param('id') id: string) {
    await firstValueFrom(
      this.httpService.delete(`${PARTY_SERVICE}/customers/${id}`),
    );
  }

  private mapToTmfResponse(data: any) {
    if (data.items) {
      return {
        '@type': 'Party',
        '@baseType': 'BaseEntity',
        ...data,
      };
    }
    return {
      '@type': 'Party',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }
}
