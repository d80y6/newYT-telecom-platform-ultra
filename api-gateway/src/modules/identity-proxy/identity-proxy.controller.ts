import { Controller, Get, Post, Put, Patch, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF656 - Identity Management')
@Controller('tmf-api/identityManagement/v5')
export class IdentityProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Post('identity')
  @ApiOperation({ summary: 'Create identity' })
  @HttpCode(201)
  async createIdentity(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity`, body),
    );
    return data;
  }

  @Get('identity')
  @ApiOperation({ summary: 'List identities' })
  @ApiQuery({ name: 'page', required: false })
  @ApiQuery({ name: 'size', required: false })
  async listIdentities(@Query('page') page?: string, @Query('size') size?: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity`, { params: { page, size } }),
    );
    return data;
  }

  @Get('identity/:id')
  @ApiOperation({ summary: 'Get identity' })
  @ApiParam({ name: 'id', description: 'Identity UUID' })
  async getIdentity(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}`),
    );
    return data;
  }

  @Put('identity/:id')
  @ApiOperation({ summary: 'Update identity' })
  @ApiParam({ name: 'id', description: 'Identity UUID' })
  async updateIdentity(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.put(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}`, body),
    );
    return data;
  }

  @Delete('identity/:id')
  @ApiOperation({ summary: 'Delete identity' })
  @ApiParam({ name: 'id', description: 'Identity UUID' })
  async deleteIdentity(@Param('id') id: string) {
    await firstValueFrom(this.httpService.delete(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}`));
    return { deleted: true };
  }

  @Patch('identity/:id/verify')
  @ApiOperation({ summary: 'Verify identity' })
  async verifyIdentity(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}/verify`, {}),
    );
    return data;
  }

  @Patch('identity/:id/primary')
  @ApiOperation({ summary: 'Set as primary' })
  async setPrimary(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}/primary`, {}),
    );
    return data;
  }

  @Patch('identity/:id/revoke')
  @ApiOperation({ summary: 'Revoke identity' })
  async revokeIdentity(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/${id}/revoke`, {}),
    );
    return data;
  }

  @Get('identity/party/:partyId')
  @ApiOperation({ summary: 'Get identities by party' })
  async getIdentitiesByParty(@Param('partyId') partyId: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/identityManagement/v5/identity/party/${partyId}`),
    );
    return data;
  }
}
