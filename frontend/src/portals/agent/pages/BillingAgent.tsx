import { useQuery, useMutation } from '@tanstack/react-query';
import { billingAPI } from '../services/api';

export default function BillingAgent() {
  const { data: payments, isLoading } = useQuery({
    queryKey: ['payments'],
    queryFn: () => billingAPI.getPayments()
  });

  const processMutation = useMutation({
    mutationFn: (id: string) => billingAPI.processPayment(id)
  });

  return (
    <div className="billing-page">
      <h2>Billing Operations</h2>
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Pending Payments</h3>
          <p className="highlight">{payments?.length || 0}</p>
        </div>
      </div>
      <h3>Payment Queue</h3>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Amount</th>
            <th>Method</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {payments?.map((p: any) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>YER {p.amount}</td>
              <td>{p.paymentMethod}</td>
              <td><span className={`status ${p.status?.toLowerCase()}`}>{p.status}</span></td>
              <td>
                {p.status === 'PENDING' && (
                  <button onClick={() => processMutation.mutate(p.id)}>Process</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
