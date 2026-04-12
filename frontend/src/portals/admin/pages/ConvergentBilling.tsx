import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

export default function ConvergentBilling() {
  const { data, isLoading } = useQuery({ queryKey: ['convergent'], queryFn: () => client.get('/convergentBilling/v5/billing').then(r => r.data) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="convergent-billing-page">
      <h2>Convergent Billing</h2>
      <div className="billing-overview">
        <div className="overview-card"><h3>Postpaid Revenue</h3><p className="value">45M YER</p></div>
        <div className="overview-card"><h3>Prepaid Revenue</h3><p className="value">28M YER</p></div>
        <div className="overview-card"><h3>Data Services</h3><p className="value">18M YER</p></div>
        <div className="overview-card"><h3>VAS Revenue</h3><p className="value">5.2M YER</p></div>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>Account</th><th>Type</th><th>Amount</th><th>Charges</th><th>Status</th></tr></thead>
        <tbody>
          {data?.map((item: any) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.accountId}</td>
              <td>{item.billingType}</td>
              <td>{item.totalAmount} YER</td>
              <td>{item.chargesBreakdown}</td>
              <td><span className={`status ${item.status?.toLowerCase()}`}>{item.status}</span></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
