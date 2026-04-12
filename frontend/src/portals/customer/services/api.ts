import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || '/tmf-api';

const client = axios.create({
  baseURL: API_BASE,
  headers: { 
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
});

export const customerAPI = {
  getAll: () => client.get('/customerManagement/v5/customer').then(r => r.data),
  getById: (id: string) => client.get(`/customerManagement/v5/customer/${id}`).then(r => r.data),
  getCurrent: () => client.get('/customerManagement/v5/customer/me').then(r => r.data),
  create: (data: any) => client.post('/customerManagement/v5/customer', data).then(r => r.data),
  update: (id: string, data: any) => client.patch(`/customerManagement/v5/customer/${id}`, data).then(r => r.data),
  delete: (id: string) => client.delete(`/customerManagement/v5/customer/${id}`),
  search: (query: string) => client.get(`/customerManagement/v5/customer?${query}`).then(r => r.data)
};

export const orderAPI = {
  getAll: () => client.get('/productOrderingManagement/v5/order').then(r => r.data),
  getById: (id: string) => client.get(`/productOrderingManagement/v5/order/${id}`).then(r => r.data),
  create: (data: any) => client.post('/productOrderingManagement/v5/order', data).then(r => r.data),
  cancel: (id: string) => client.post(`/productOrderingManagement/v5/order/${id}/cancel`).then(r => r.data)
};

export const serviceOrderAPI = {
  getAll: () => client.get('/serviceOrderingManagement/v4/serviceOrder').then(r => r.data),
  getById: (id: string) => client.get(`/serviceOrderingManagement/v4/serviceOrder/${id}`).then(r => r.data),
  create: (data: any) => client.post('/serviceOrderingManagement/v4/serviceOrder', data).then(r => r.data)
};

export const billingAPI = {
  getInvoices: () => client.get('/customerBillManagement/v5/bill').then(r => r.data),
  getInvoiceById: (id: string) => client.get(`/customerBillManagement/v5/bill/${id}`).then(r => r.data),
  getMyBills: () => client.get('/customerBillManagement/v5/bill').then(r => r.data),
  getPayments: () => client.get('/customerBillManagement/v5/payment').then(r => r.data),
  createPayment: (data: any) => client.post('/customerBillManagement/v5/payment', data).then(r => r.data),
  processPayment: (id: string) => client.post(`/customerBillManagement/v5/payment/${id}/process`).then(r => r.data)
};

export const subscriptionAPI = {
  getAll: () => client.get('/serviceOrderingManagement/v4/serviceOrder').then(r => r.data),
  getMyServices: () => client.get('/serviceOrderingManagement/v4/serviceOrder').then(r => r.data),
  create: (data: any) => client.post('/serviceOrderingManagement/v4/serviceOrder', data).then(r => r.data)
};

export const productAPI = {
  getCatalog: () => client.get('/productCatalogManagement/v5/productOffering').then(r => r.data),
  getById: (id: string) => client.get(`/productCatalogManagement/v5/productOffering/${id}`).then(r => r.data),
  search: (query: string) => client.get(`/productCatalogManagement/v5/productOffering?${query}`).then(r => r.data)
};

export const troubleTicketAPI = {
  getAll: () => client.get('/troubleTicketManagement/v5/troubleTicket').then(r => r.data),
  getById: (id: string) => client.get(`/troubleTicketManagement/v5/troubleTicket/${id}`).then(r => r.data),
  create: (data: any) => client.post('/troubleTicketManagement/v5/troubleTicket', data).then(r => r.data),
  addNote: (id: string, note: string) => client.post(`/troubleTicketManagement/v5/troubleTicket/${id}/notes`, { note }).then(r => r.data)
};

export const usageAPI = {
  getUsage: (subId: string) => client.get(`/usageManagement/v5/usage?subscriptionId=${subId}`).then(r => r.data),
  getRating: (id: string) => client.get(`/usageManagement/v5/rating/${id}`).then(r => r.data),
  getMyUsage: () => client.get('/usageManagement/v5/usage').then(r => r.data)
};

export const partyAPI = {
  getCurrent: () => client.get('/partyManagement/v5/party').then(r => r.data),
  update: (data: any) => client.patch('/partyManagement/v5/party', data).then(r => r.data)
};

export const notificationAPI = {
  getAll: () => client.get('/notificationManagement/v5 notification').then(r => r.data),
  markRead: (id: string) => client.patch(`/notificationManagement/v5/notification/${id}`, { read: true }).then(r => r.data)
};

export const inventoryAPI = {
  getProducts: () => client.get('/productInventory/v5/product').then(r => r.data),
  getById: (id: string) => client.get(`/productInventory/v5/product/${id}`).then(r => r.data)
};

export const shoppingCartAPI = {
  getAll: () => client.get('/shoppingCart/v5/cart').then(r => r.data),
  getById: (id: string) => client.get(`/shoppingCart/v5/cart/${id}`).then(r => r.data),
  create: (data: any) => client.post('/shoppingCart/v5/cart', data).then(r => r.data),
  updateStatus: (id: string, status: string) => client.patch(`/shoppingCart/v5/cart/${id}/status`, { status }).then(r => r.data)
};

export default client;
