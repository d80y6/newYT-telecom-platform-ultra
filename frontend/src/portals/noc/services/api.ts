import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || '/tmf-api';

const client = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' }
});

export const networkAPI = {
  getElements: () => client.get('/resourceInventoryManagement/v4/resource').then(r => r.data),
  getAlarms: () => client.get('/faultManagement/v5/fault').then(r => r.data),
  getPerformance: () => client.get('/performanceManagement/v5/performance').then(r => r.data),
  acknowledgeAlarm: (id: string) => client.post(`/faultManagement/v5/fault/${id}/acknowledge`).then(r => r.data),
  clearAlarm: (id: string) => client.post(`/faultManagement/v5/fault/${id}/clear`).then(r => r.data)
};

export default client;
