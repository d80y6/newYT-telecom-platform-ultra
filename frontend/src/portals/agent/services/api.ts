import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || '/tmf-api';

const client = axios.create({
  baseURL: API_BASE,
  headers: { 
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('agentToken')}`
  }
});

export const customerAPI = {
  getAll: () => client.get('/customerManagement/v5/customer').then(r => r.data),
  getById: (id: string) => client.get(`/customerManagement/v5/customer/${id}`).then(r => r.data),
  search: (query: string) => client.get(`/customerManagement/v5/customer?${query}`).then(r => r.data),
  create: (data: any) => client.post('/customerManagement/v5/customer', data).then(r => r.data),
  update: (id: string, data: any) => client.patch(`/customerManagement/v5/customer/${id}`, data).then(r => r.data)
};

export const orderAPI = {
  getAll: () => client.get('/productOrderingManagement/v5/order').then(r => r.data),
  getById: (id: string) => client.get(`/productOrderingManagement/v5/order/${id}`).then(r => r.data),
  create: (data: any) => client.post('/productOrderingManagement/v5/order', data).then(r => r.data),
  approve: (id: string) => client.post(`/productOrderingManagement/v5/order/${id}/approve`).then(r => r.data),
  reject: (id: string, reason: string) => client.post(`/productOrderingManagement/v5/order/${id}/reject`, { reason }).then(r => r.data),
  cancel: (id: string) => client.post(`/productOrderingManagement/v5/order/${id}/cancel`).then(r => r.data)
};

export const serviceOrderAPI = {
  getAll: () => client.get('/serviceOrderingManagement/v4/serviceOrder').then(r => r.data),
  create: (data: any) => client.post('/serviceOrderingManagement/v4/serviceOrder', data).then(r => r.data)
};

export const billingAPI = {
  getInvoices: () => client.get('/customerBillManagement/v5/bill').then(r => r.data),
  getPayments: () => client.get('/customerBillManagement/v5/payment').then(r => r.data),
  createPayment: (data: any) => client.post('/customerBillManagement/v5/payment', data).then(r => r.data),
  processPayment: (id: string) => client.post(`/customerBillManagement/v5/payment/${id}/process`).then(r => r.data)
};

export const troubleTicketAPI = {
  getAll: () => client.get('/troubleTicketManagement/v5/troubleTicket').then(r => r.data),
  getById: (id: string) => client.get(`/troubleTicketManagement/v5/troubleTicket/${id}`).then(r => r.data),
  create: (data: any) => client.post('/troubleTicketManagement/v5/troubleTicket', data).then(r => r.data),
  update: (id: string, data: any) => client.patch(`/troubleTicketManagement/v5/troubleTicket/${id}`, data).then(r => r.data),
  addNote: (id: string, note: string) => client.post(`/troubleTicketManagement/v5/troubleTicket/${id}/notes`, { note }).then(r => r.data)
};

export const productAPI = {
  getCatalog: () => client.get('/productCatalogManagement/v5/productOffering').then(r => r.data),
  search: (query: string) => client.get(`/productCatalogManagement/v5/productOffering?${query}`).then(r => r.data)
};

export const provisioningAPI = {
  getAll: () => client.get('/serviceOrderingManagement/v4/serviceOrder').then(r => r.data),
  activate: (id: string) => client.post(`/serviceOrderingManagement/v4/serviceOrder/${id}/activate`).then(r => r.data),
  deactivate: (id: string) => client.post(`/serviceOrderingManagement/v4/serviceOrder/${id}/deactivate`).then(r => r.data)
};

export const identityAPI = {
  getIdentities: () => client.get('/identityManagement/v5/identity').then(r => r.data),
  getIdentityById: (id: string) => client.get(`/identityManagement/v5/identity/${id}`).then(r => r.data),
  createIdentity: (data: any) => client.post('/identityManagement/v5/identity', data).then(r => r.data),
  verifyIdentity: (id: string) => client.post(`/identityManagement/v5/identity/${id}/verify`).then(r => r.data)
};

export const appointmentAPI = {
  getAppointments: () => client.get('/appointmentManagement/v5/appointment').then(r => r.data),
  getAppointmentById: (id: string) => client.get(`/appointmentManagement/v5/appointment/${id}`).then(r => r.data),
  createAppointment: (data: any) => client.post('/appointmentManagement/v5/appointment', data).then(r => r.data),
  updateStatus: (id: string, status: string) => client.patch(`/appointmentManagement/v5/appointment/${id}/status`, { status }).then(r => r.data)
};

export default client;
