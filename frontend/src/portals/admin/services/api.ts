import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || '/tmf-api';

const client = axios.create({
  baseURL: API_BASE,
  headers: { 
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
  }
});

export const adminAPI = {
  getUsers: () => client.get('/admin/users').then(r => r.data),
  createUser: (data: any) => client.post('/admin/users', data).then(r => r.data),
  updateUser: (id: string, data: any) => client.patch(`/admin/users/${id}`, data).then(r => r.data),
  deleteUser: (id: string) => client.delete(`/admin/users/${id}`).then(r => r.data),
  
  getProducts: () => client.get('/productCatalogManagement/v5/productOffering').then(r => r.data),
  createProduct: (data: any) => client.post('/productCatalogManagement/v5/productOffering', data).then(r => r.data),
  updateProduct: (id: string, data: any) => client.patch(`/productCatalogManagement/v5/productOffering/${id}`, data).then(r => r.data),
  
  getConfig: () => client.get('/admin/config').then(r => r.data),
  updateConfig: (data: any) => client.patch('/admin/config', data).then(r => r.data),
  
  getAuditLogs: () => client.get('/admin/audit').then(r => r.data),
  getReports: (type: string) => client.get(`/admin/reports/${type}`).then(r => r.data)
};

export default client;
