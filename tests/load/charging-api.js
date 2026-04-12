import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const CHARGING_URL = __ENV.CHARGING_URL || 'http://localhost:8081';

export const options = {
  stages: [
    { duration: '1m', target: 100 },
    { duration: '3m', target: 200 },
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<200', 'p(99)<500'],
    http_req_failed: ['rate<0.001'],
  },
};

export default function () {
  const headers = { 'Content-Type': 'application/json' };
  const accountId = '00000000-0000-0000-0000-000000000001';

  // Get balance (direct to Go engine - should be <5ms)
  const balanceRes = http.get(
    `${CHARGING_URL}/api/v1/balance/${accountId}`,
    { headers }
  );

  check(balanceRes, {
    'get balance status 200': (r) => r.status === 200,
    'balance response time <50ms': (r) => r.timings.duration < 50,
  });

  // Reserve balance
  const reserveRes = http.post(
    `${CHARGING_URL}/api/v1/balance/${accountId}/reserve`,
    JSON.stringify({ amount: 10.00, currency: 'YER' }),
    { headers }
  );

  check(reserveRes, {
    'reserve status 200': (r) => r.status === 200,
    'reserve has id': (r) => r.json('reservationId') !== undefined,
  });

  if (reserveRes.status === 200) {
    const reservationId = reserveRes.json('reservationId');

    // Confirm deduction
    const confirmRes = http.post(
      `${CHARGING_URL}/api/v1/balance/${accountId}/confirm`,
      JSON.stringify({ reservationId, amount: 10.00 }),
      { headers }
    );

    check(confirmRes, {
      'confirm status 200': (r) => r.status === 200,
    });
  }

  sleep(0.1);
}
