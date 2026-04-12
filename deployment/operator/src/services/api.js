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
};

export const troubleTicketApi = {
  getAll: () => api.get('/tmf-api/troubleTicketManagement/v5/troubleTicket'),
  getById: (id) => api.get(`/tmf-api/troubleTicketManagement/v5/troubleTicket/${id}`),
  create: (data) => api.post('/tmf-api/troubleTicketManagement/v5/troubleTicket', data),
  update: (id, data) => api.put(`/tmf-api/troubleTicketManagement/v5/troubleTicket/${id}`, data),
};

export const inventoryApi = {
  getResources: () => api.get('/tmf-api/resourceInventoryManagement/v4/resource'),
  getResource: (id) => api.get(`/tmf-api/resourceInventoryManagement/v4/resource/${id}`),
  create: (data) => api.post('/tmf-api/resourceInventoryManagement/v4/resource', data),
  update: (id, data) => api.put(`/tmf-api/resourceInventoryManagement/v4/resource/${id}`, data),
};

export const subscriptionApi = {
  getAll: () => api.get('/tmf-api/productInventory/v5/productInventory'),
  getById: (id) => api.get(`/tmf-api/productInventory/v5/productInventory/${id}`),
  update: (id, data) => api.put(`/tmf-api/productInventory/v5/productInventory/${id}`, data),
};

export const notificationApi = {
  getAll: () => api.get('/tmf-api/notificationListener/v5/notification'),
};

export const provisioningApi = {
  getOrders: () => api.get('/tmf-api/serviceOrderingManagement/v4/serviceOrder'),
  create: (data) => api.post('/tmf-api/serviceOrderingManagement/v4/serviceOrder', data),
  update: (id, data) => api.put(`/tmf-api/serviceOrderingManagement/v4/serviceOrder/${id}`, data),
};