import { HttpService } from '@nestjs/axios';
export declare class AppointmentProxyController {
    private readonly httpService;
    constructor(httpService: HttpService);
    createAppointment(body: any): Promise<any>;
    listAppointments(page?: string, size?: string): Promise<any>;
    getAppointment(id: string): Promise<any>;
    updateAppointment(id: string, body: any): Promise<any>;
    deleteAppointment(id: string): Promise<{
        deleted: boolean;
    }>;
    startAppointment(id: string): Promise<any>;
    completeAppointment(id: string): Promise<any>;
    cancelAppointment(id: string): Promise<any>;
    getAppointmentsByCustomer(customerId: string): Promise<any>;
    getAppointmentsByTechnician(technicianId: string): Promise<any>;
}
