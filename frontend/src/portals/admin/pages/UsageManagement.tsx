import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

export default function UsageManagement() {
  const { data, isLoading } = useQuery({ queryKey: ['usage'], queryFn: () => client.get('/usageManagement/v5/usage').then(r => r.data) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="usage-page">
      <h2>Usage Management</h2>
      <div className="usage-summary">
        <div className="summary-card"><h3>Total Usage</h3><p className="value">{data?.length || 0} records</p></div>
        <div className="summary-card"><h3>Data Usage</h3><p className="value">1.2 TB</p></div>
        <div className="summary-card"><h3>Voice Usage</h3><p className="value">45K min</p></div>
        <div className="summary-card"><h3>SMS Usage</h3><p className="value">12K</p></div>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>Subscription</th><th>Type</th><th>Amount</th><th>Period</th><th>Status</th></tr></thead>
        <tbody>
          {data?.map((item: any) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.subscriptionId}</td>
              <td>{item.usageType}</td>
              <td>{item.amount} {item.unit}</td>
              <td>{item.period}</td>
              <td><span className="status processed">Processed</span></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
