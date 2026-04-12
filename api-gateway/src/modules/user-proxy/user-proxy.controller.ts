import { Controller, Get, Post, Put, Patch, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF653 - User Management')
@Controller('tmf-api/userManagement/v5')
export class UserProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Post('user')
  @ApiOperation({ summary: 'Create user' })
  @HttpCode(201)
  async createUser(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user`, body),
    );
    return data;
  }

  @Get('user')
  @ApiOperation({ summary: 'List users' })
  async listUsers(@Query('page') page?: string, @Query('size') size?: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user`, { params: { page, size } }),
    );
    return data;
  }

  @Get('user/:id')
  @ApiOperation({ summary: 'Get user' })
  async getUser(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}`),
    );
    return data;
  }

  @Put('user/:id')
  @ApiOperation({ summary: 'Update user' })
  async updateUser(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.put(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}`, body),
    );
    return data;
  }

  @Delete('user/:id')
  @ApiOperation({ summary: 'Delete user' })
  async deleteUser(@Param('id') id: string) {
    await firstValueFrom(this.httpService.delete(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}`));
    return { deleted: true };
  }

  @Patch('user/:id/suspend')
  @ApiOperation({ summary: 'Suspend user' })
  async suspendUser(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}/suspend`, {}),
    );
    return data;
  }

  @Patch('user/:id/activate')
  @ApiOperation({ summary: 'Activate user' })
  async activateUser(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/userManagement/v5/user/${id}/activate`, {}),
    );
    return data;
  }
}
