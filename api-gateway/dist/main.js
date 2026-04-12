"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const core_1 = require("@nestjs/core");
const swagger_1 = require("@nestjs/swagger");
const app_module_1 = require("./app.module");
async function bootstrap() {
    const app = await core_1.NestFactory.create(app_module_1.AppModule);
    const config = new swagger_1.DocumentBuilder()
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
    const document = swagger_1.SwaggerModule.createDocument(app, config);
    swagger_1.SwaggerModule.setup('api-docs', app, document);
    await app.listen(3000);
    console.log('API Gateway running on http://localhost:3000');
    console.log('Swagger docs at http://localhost:3000/api-docs');
}
bootstrap();
//# sourceMappingURL=main.js.map