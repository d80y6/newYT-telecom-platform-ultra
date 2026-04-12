import { NestFactory } from '@nestjs/core';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  const config = new DocumentBuilder()
    .setTitle('Yemen PTC BSS/OSS API Gateway')
    .setDescription('TM Forum Open API Gateway - Unified interface for all TMF APIs')
    .setVersion('5.0')
    .addTag('TMF632', 'Party Management')
    .addTag('TMF620', 'Product Catalog Management')
    .addTag('TMF622', 'Product Ordering Management')
    .addTag('TMF647', 'Customer Bill Management')
    .addTag('TMF654', 'Prepay Balance Management')
    .addServer('http://localhost:3000', 'Local')
    .addServer('https://api.yemenptc.com', 'Production')
    .build();

  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('api-docs', app, document);

  await app.listen(3000);
  console.log('API Gateway running on http://localhost:3000');
  console.log('Swagger docs at http://localhost:3000/api-docs');
}

bootstrap();
