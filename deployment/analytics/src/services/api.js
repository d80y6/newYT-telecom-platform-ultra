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
};

export const customerApi = {
  getAll: () => api.get('/tmf-api/customerManagement/v5/customer'),
  getById: (id) => api.get(`/tmf-api/customerManagement/v5/customer/${id}`),
};

export const accountApi = {
  getAll: () => api.get('/tmf-api/customerManagement/v5/account'),
};

export const billingApi = {
  getBills: () => api.get('/tmf-api/customerBillManagement/v5/invoice'),
  getAccounts: () => api.get('/tmf-api/customerBillManagement/v5/billingAccount'),
  getPayments: () => api.get('/tmf-api/customerBillManagement/v5/payment'),
};

export const productApi = {
  getOfferings: () => api.get('/tmf-api/productCatalogManagement/v5/productOffering'),
  getCatalog: () => api.get('/tmf-api/productCatalogManagement/v5/catalog'),
};

export const orderApi = {
  getOrders: () => api.get('/tmf-api/productOrderingManagement/v5/order'),
};

export const subscriptionApi = {
  getAll: () => api.get('/tmf-api/productInventory/v5/productInventory'),
};

export const usageApi = {
  getUsage: () => api.get('/tmf-api/usageManagement/v5/usage'),
};