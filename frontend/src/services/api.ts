import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/tmf-api';

const client = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' }
});

export const customerAPI = {
  getAll: () => client.get('/customerManagement/v5/customer').then(r => r.data),
  getById: (id: string) => client.get(`/customerManagement/v5/customer/${id}`).then(r => r.data),
  create: (data: any) => client.post('/customerManagement/v5/customer', data).then(r => r.data),
  update: (id: string, data: any) => client.patch(`/customerManagement/v5/customer/${id}`, data).then(r => r.data),
  delete: (id: string) => client.delete(`/customerManagement/v5/customer/${id}`)
};

export const orderAPI = {
  getAll: () => client.get('/productOrderingManagement/v5/order').then(r => r.data),
  getById: (id: string) => client.get(`/productOrderingManagement/v5/order/${id}`).then(r => r.data),
  create: (data: any) => client.post('/productOrderingManagement/v5/order', data).then(r => r.data)
};

export const billingAPI = {
  getInvoices: () => client.get('/customerBillManagement/v5/bill').then(r => r.data),
  getPayments: () => client.get('/customerBillManagement/v5/payment').then(r => r.data),
  createPayment: (data: any) => client.post('/customerBillManagement/v5/payment', data).then(r => r.data)
};

export const subscriptionAPI = {
  getAll: () => client.get('/serviceOrderingManagement/v4/serviceOrder').then(r => r.data),
  create: (data: any) => client.post('/serviceOrderingManagement/v4/serviceOrder', data).then(r => r.data)
};

// Phase 3: Marketing & Sales APIs
export const productPriceAPI = {
  getAll: () => client.get('/productCatalogManagement/v5/productPrice').then(r => r.data),
  getById: (id: string) => client.get(`/productCatalogManagement/v5/productPrice/${id}`).then(r => r.data),
  create: (data: any) => client.post('/productCatalogManagement/v5/productPrice', data).then(r => r.data),
  update: (id: string, data: any) => client.put(`/productCatalogManagement/v5/productPrice/${id}`, data).then(r => r.data),
  delete: (id: string) => client.delete(`/productCatalogManagement/v5/productPrice/${id}`)
};

export const pricePlanAPI = {
  getAll: () => client.get('/productCatalogManagement/v5/pricePlan').then(r => r.data),
  getById: (id: string) => client.get(`/productCatalogManagement/v5/pricePlan/${id}`).then(r => r.data),
  create: (data: any) => client.post('/productCatalogManagement/v5/pricePlan', data).then(r => r.data)
};

export const salesLeadAPI = {
  getAll: () => client.get('/salesLeadManagement/v5').then(r => r.data),
  getById: (id: string) => client.get(`/salesLeadManagement/v5/${id}`).then(r => r.data),
  create: (data: any) => client.post('/salesLeadManagement/v5', data).then(r => r.data),
  updateStatus: (id: string, status: string) => client.patch(`/salesLeadManagement/v5/${id}/status`, { status }).then(r => r.data)
};

export const quoteAPI = {
  getAll: () => client.get('/quoteManagement/v5/quote').then(r => r.data),
  getById: (id: string) => client.get(`/quoteManagement/v5/quote/${id}`).then(r => r.data),
  create: (data: any) => client.post('/quoteManagement/v5/quote', data).then(r => r.data),
  updateStatus: (id: string, status: string) => client.patch(`/quoteManagement/v5/quote/${id}/status`, { status }).then(r => r.data)
};

export const shoppingCartAPI = {
  getAll: () => client.get('/shoppingCart/v5/cart').then(r => r.data),
  getById: (id: string) => client.get(`/shoppingCart/v5/cart/${id}`).then(r => r.data),
  create: (data: any) => client.post('/shoppingCart/v5/cart', data).then(r => r.data),
  updateStatus: (id: string, status: string) => client.patch(`/shoppingCart/v5/cart/${id}/status`, { status }).then(r => r.data)
};

// Phase 4: Analytics API
export const analyticsAPI = {
  getMetrics: (category?: string) => client.get('/analytics/v5/metric', { params: { category } }).then(r => r.data),
  getMetricById: (id: string) => client.get(`/analytics/v5/metric/${id}`).then(r => r.data),
  createMetric: (data: any) => client.post('/analytics/v5/metric', data).then(r => r.data),
  getDashboard: () => client.get('/analytics/v5/dashboard').then(r => r.data),
  getKpis: () => client.get('/analytics/v5/kpi').then(r => r.data),
  getTimeSeries: (metricName: string, startTime: string, endTime: string) => 
    client.get('/analytics/v5/timeSeries', { params: { metricName, startTime, endTime } }).then(r => r.data)
};

// Identity & User Management APIs (Phase 2)
export const identityAPI = {
  getIdentities: () => client.get('/identityManagement/v5/identity').then(r => r.data),
  getIdentityById: (id: string) => client.get(`/identityManagement/v5/identity/${id}`).then(r => r.data),
  createIdentity: (data: any) => client.post('/identityManagement/v5/identity', data).then(r => r.data),
  verifyIdentity: (id: string) => client.post(`/identityManagement/v5/identity/${id}/verify`).then(r => r.data)
};

export const userAPI = {
  getUsers: () => client.get('/userManagement/v5/user').then(r => r.data),
  getUserById: (id: string) => client.get(`/userManagement/v5/user/${id}`).then(r => r.data),
  createUser: (data: any) => client.post('/userManagement/v5/user', data).then(r => r.data),
  updateUser: (id: string, data: any) => client.patch(`/userManagement/v5/user/${id}`, data).then(r => r.data)
};

export const appointmentAPI = {
  getAppointments: () => client.get('/appointmentManagement/v5/appointment').then(r => r.data),
  getAppointmentById: (id: string) => client.get(`/appointmentManagement/v5/appointment/${id}`).then(r => r.data),
  createAppointment: (data: any) => client.post('/appointmentManagement/v5/appointment', data).then(r => r.data),
  updateStatus: (id: string, status: string) => client.patch(`/appointmentManagement/v5/appointment/${id}/status`, { status }).then(r => r.data)
};
