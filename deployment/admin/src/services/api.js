const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8000/api/v1';

export const api = {
  async get(endpoint) {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return response.json();
  },

  async post(endpoint, data) {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return response.json();
  },

  async put(endpoint, data) {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return response.json();
  },

  async delete(endpoint) {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      method: 'DELETE',
      headers: {
        'Accept': 'application/json',
      },
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
  },
};

export const customerApi = {
  getAll: () => api.get('/tmf-api/customerManagement/v5/customer'),
  getById: (id) => api.get(`/tmf-api/customerManagement/v5/customer/${id}`),
  create: (data) => api.post('/tmf-api/customerManagement/v5/customer', data),
  update: (id, data) => api.put(`/tmf-api/customerManagement/v5/customer/${id}`, data),
  delete: (id) => api.delete(`/tmf-api/customerManagement/v5/customer/${id}`),
};

export const accountApi = {
  getAll: () => api.get('/tmf-api/customerManagement/v5/account'),
  getById: (id) => api.get(`/tmf-api/customerManagement/v5/account/${id}`),
  create: (data) => api.post('/tmf-api/customerManagement/v5/account', data),
  update: (id, data) => api.put(`/tmf-api/customerManagement/v5/account/${id}`, data),
};

export const billingApi = {
  getBills: () => api.get('/tmf-api/customerBillManagement/v5/invoice'),
  getBill: (id) => api.get(`/tmf-api/customerBillManagement/v5/invoice/${id}`),
  getAccounts: () => api.get('/tmf-api/customerBillManagement/v5/billingAccount'),
  createBill: (data) => api.post('/tmf-api/customerBillManagement/v5/invoice', data),
  getPayments: () => api.get('/tmf-api/customerBillManagement/v5/payment'),
};

export const productApi = {
  getCatalog: () => api.get('/tmf-api/productCatalogManagement/v5/catalog'),
  getOfferings: () => api.get('/tmf-api/productCatalogManagement/v5/productOffering'),
  getOffering: (id) => api.get(`/tmf-api/productCatalogManagement/v5/productOffering/${id}`),
  createOffering: (data) => api.post('/tmf-api/productCatalogManagement/v5/productOffering', data),
  updateOffering: (id, data) => api.put(`/tmf-api/productCatalogManagement/v5/productOffering/${id}`, data),
  getSpecs: () => api.get('/tmf-api/productCatalogManagement/v5/productSpecification'),
};

export const orderApi = {
  getOrders: () => api.get('/tmf-api/productOrderingManagement/v5/order'),
  getOrder: (id) => api.get(`/tmf-api/productOrderingManagement/v5/order/${id}`),
  create: (data) => api.post('/tmf-api/productOrderingManagement/v5/order', data),
  update: (id, data) => api.put(`/tmf-api/productOrderingManagement/v5/order/${id}`, data),
  cancel: (id) => api.post(`/tmf-api/productOrderingManagement/v5/order/${id}/cancel`, {}),
};

export const subscriptionApi = {
  getAll: () => api.get('/tmf-api/productInventory/v5/productInventory'),
  getById: (id) => api.get(`/tmf-api/productInventory/v5/productInventory/${id}`),
  update: (id, data) => api.put(`/tmf-api/productInventory/v5/productInventory/${id}`, data),
};

export const troubleTicketApi = {
  getAll: () => api.get('/tmf-api/troubleTicketManagement/v5/troubleTicket'),
  getById: (id) => api.get(`/tmf-api/troubleTicketManagement/v5/troubleTicket/${id}`),
  create: (data) => api.post('/tmf-api/troubleTicketManagement/v5/troubleTicket', data),
  update: (id, data) => api.put(`/tmf-api/troubleTicketManagement/v5/troubleTicket/${id}`, data),
};

export const notificationApi = {
  getAll: () => api.get('/tmf-api/notificationListener/v5/notification'),
  subscribe: (data) => api.post('/tmf-api/notificationListener/v5/subscription', data),
};

export const inventoryApi = {
  getResources: () => api.get('/tmf-api/resourceInventoryManagement/v4/resource'),
  getResource: (id) => api.get(`/tmf-api/resourceInventoryManagement/v4/resource/${id}`),
  create: (data) => api.post('/tmf-api/resourceInventoryManagement/v4/resource', data),
  update: (id, data) => api.put(`/tmf-api/resourceInventoryManagement/v4/resource/${id}`, data),
};