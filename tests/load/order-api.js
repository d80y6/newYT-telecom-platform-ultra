import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '1m', target: 30 },
    { duration: '3m', target: 50 },
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const headers = { 'Content-Type': 'application/json' };

  // Create order
  const orderPayload = JSON.stringify({
    customerId: '00000000-0000-0000-0000-000000000001',
    orderType: 'ACQUISITION',
    channel: 'WEB',
    items: [
      { offeringId: 'ftth-100mbps', action: 'add' }
    ],
  });

  const createRes = http.post(
    `${BASE_URL}/tmf-api/productOrderingManagement/v5/productOrder`,
    orderPayload,
    { headers }
  );

  check(createRes, {
    'create order status 201': (r) => r.status === 201,
    'create order has id': (r) => r.json('data.id') !== undefined,
  });

  if (createRes.status === 201) {
    const orderId = createRes.json('data.id');

    // Get order
    const getRes = http.get(
      `${BASE_URL}/tmf-api/productOrderingManagement/v5/productOrder/${orderId}`,
      { headers }
    );

    check(getRes, {
      'get order status 200': (r) => r.status === 200,
    });

    // Execute saga
    const executeRes = http.post(
      `${BASE_URL}/tmf-api/productOrderingManagement/v5/productOrder/${orderId}/execute`,
      null,
      { headers }
    );

    check(executeRes, {
      'execute saga status 200': (r) => r.status === 200,
      'saga completed': (r) => r.json('data.status') === 'COMPLETED',
    });
  }

  sleep(1);
}
