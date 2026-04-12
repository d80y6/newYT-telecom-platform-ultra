import { Controller, Get, Post, Param, Query, Body, HttpCode, Patch } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const QUOTE_SERVICE = process.env.QUOTE_SERVICE_URL || 'http://bss-core:8080/api/v1';

@ApiTags('TMF694 - Quote')
@Controller('tmf-api/quoteManagement/v5')
export class QuoteProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Get()
  @ApiOperation({ summary: 'List quotes' })
  async listQuotes(
    @Query('offset') offset?: string,
    @Query('limit') limit?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${QUOTE_SERVICE}/quote`, {
        params: { offset, limit },
      }),
    );
    return this.mapToTmfResponse(data);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Retrieve a quote' })
  @ApiParam({ name: 'id', description: 'Quote UUID' })
  async getQuote(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${QUOTE_SERVICE}/quote/${id}`),
    );
    return this.mapToTmfResponse(data);
  }

  @Post()
  @ApiOperation({ summary: 'Create a quote' })
  @HttpCode(201)
  async createQuote(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${QUOTE_SERVICE}/quote`, body),
    );
    return this.mapToTmfResponse(data);
  }

  @Patch(':id/status')
  @ApiOperation({ summary: 'Update quote status' })
  async updateQuoteStatus(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${QUOTE_SERVICE}/quote/${id}/status`, body),
    );
    return this.mapToTmfResponse(data);
  }

  private mapToTmfResponse(data: any) {
    return {
      '@type': 'Quote',
      '@baseType': 'BaseEntity',
      ...data,
    };
  }
}
