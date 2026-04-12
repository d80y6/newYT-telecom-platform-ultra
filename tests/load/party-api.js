import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '1m', target: 50 },
    { duration: '3m', target: 100 },
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const headers = { 'Content-Type': 'application/json' };

  // Create customer
  const customerPayload = JSON.stringify({
    firstName: 'Test',
    lastName: 'User',
    primaryPhone: '+96777' + Math.floor(Math.random() * 10000000),
    customerType: 'RESIDENTIAL',
    city: 'Sana\'a',
    governorate: 'Sana\'a',
  });

  const createRes = http.post(
    `${BASE_URL}/tmf-api/customerManagement/v5/party`,
    customerPayload,
    { headers }
  );

  check(createRes, {
    'create customer status 201': (r) => r.status === 201,
    'create customer has id': (r) => r.json('data.id') !== undefined,
  });

  if (createRes.status === 201) {
    const customerId = createRes.json('data.id');

    // Get customer
    const getRes = http.get(
      `${BASE_URL}/tmf-api/customerManagement/v5/party/${customerId}`,
      { headers }
    );

    check(getRes, {
      'get customer status 200': (r) => r.status === 200,
      'get customer correct id': (r) => r.json('data.id') === customerId,
    });
  }

  sleep(0.5);
}
