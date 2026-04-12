import { Controller, Get, Post, Put, Patch, Delete, Param, Query, Body, HttpCode } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ApiTags, ApiOperation, ApiParam, ApiQuery } from '@nestjs/swagger';
import { firstValueFrom } from 'rxjs';

const BSS_CORE_URL = process.env.BSS_CORE_URL || 'http://bss-core:8080';

@ApiTags('TMF634 - Resource Catalog Management')
@Controller('tmf-api/resourceCatalogManagement/v5')
export class ResourceCatalogProxyController {
  constructor(private readonly httpService: HttpService) {}

  @Post('resourceCatalog')
  @ApiOperation({ summary: 'Create resource catalog' })
  @HttpCode(201)
  async createCatalog(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog`, body),
    );
    return data;
  }

  @Get('resourceCatalog')
  @ApiOperation({ summary: 'List resource catalogs' })
  @ApiQuery({ name: 'page', required: false })
  @ApiQuery({ name: 'size', required: false })
  @ApiQuery({ name: 'sortBy', required: false })
  @ApiQuery({ name: 'sortOrder', required: false })
  async listCatalogs(
    @Query('page') page?: string,
    @Query('size') size?: string,
    @Query('sortBy') sortBy?: string,
    @Query('sortOrder') sortOrder?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog`, {
        params: { page, size, sortBy, sortOrder },
      }),
    );
    return data;
  }

  @Get('resourceCatalog/:id')
  @ApiOperation({ summary: 'Get resource catalog' })
  @ApiParam({ name: 'id', description: 'Catalog UUID' })
  async getCatalog(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}`),
    );
    return data;
  }

  @Put('resourceCatalog/:id')
  @ApiOperation({ summary: 'Update resource catalog' })
  @ApiParam({ name: 'id', description: 'Catalog UUID' })
  async updateCatalog(@Param('id') id: string, @Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.put(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}`, body),
    );
    return data;
  }

  @Delete('resourceCatalog/:id')
  @ApiOperation({ summary: 'Delete resource catalog' })
  @ApiParam({ name: 'id', description: 'Catalog UUID' })
  async deleteCatalog(@Param('id') id: string) {
    await firstValueFrom(
      this.httpService.delete(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}`),
    );
    return { deleted: true };
  }

  @Patch('resourceCatalog/:id/activate')
  @ApiOperation({ summary: 'Activate resource catalog' })
  @ApiParam({ name: 'id', description: 'Catalog UUID' })
  async activateCatalog(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}/activate`, {}),
    );
    return data;
  }

  @Patch('resourceCatalog/:id/deprecate')
  @ApiOperation({ summary: 'Deprecate resource catalog' })
  @ApiParam({ name: 'id', description: 'Catalog UUID' })
  async deprecateCatalog(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceCatalog/${id}/deprecate`, {}),
    );
    return data;
  }

  @Post('resourceSpecification')
  @ApiOperation({ summary: 'Create resource specification' })
  @HttpCode(201)
  async createSpecification(@Body() body: any) {
    const { data } = await firstValueFrom(
      this.httpService.post(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceSpecification`, body),
    );
    return data;
  }

  @Get('resourceSpecification')
  @ApiOperation({ summary: 'List resource specifications' })
  @ApiQuery({ name: 'page', required: false })
  @ApiQuery({ name: 'size', required: false })
  async listSpecifications(
    @Query('page') page?: string,
    @Query('size') size?: string,
  ) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceSpecification`, {
        params: { page, size },
      }),
    );
    return data;
  }

  @Get('resourceSpecification/resourceType/:resourceType')
  @ApiOperation({ summary: 'Get specifications by resource type' })
  @ApiParam({ name: 'resourceType', description: 'Resource type' })
  async getSpecificationsByType(@Param('resourceType') resourceType: string) {
    const { data } = await firstValueFrom(
      this.httpService.get(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceSpecification/resourceType/${resourceType}`),
    );
    return data;
  }

  @Patch('resourceSpecification/:id/activate')
  @ApiOperation({ summary: 'Activate resource specification' })
  @ApiParam({ name: 'id', description: 'Specification UUID' })
  async activateSpecification(@Param('id') id: string) {
    const { data } = await firstValueFrom(
      this.httpService.patch(`${BSS_CORE_URL}/tmf-api/resourceCatalogManagement/v5/resourceSpecification/${id}/activate`, {}),
    );
    return data;
  }
}
