import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

export default function Customer360() {
  const { data, isLoading } = useQuery({ queryKey: ['customer360'], queryFn: () => client.get('/customer360/v5/summary').then(r => r.data) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="customer-360-page">
      <h2>Customer 360 View</h2>
      <div className="customer-profile">
        <div className="profile-header">
          <div className="avatar">👤</div>
          <div className="info">
            <h3>{data?.name || 'Customer Name'}</h3>
            <p>ID: {data?.id}</p>
            <p>Segment: {data?.segment}</p>
          </div>
        </div>
      </div>
      <div className="customer-insights">
        <div className="insight-card">
          <h3>Account Balance</h3>
          <p className="value">{data?.balance || '0'} YER</p>
        </div>
        <div className="insight-card">
          <h3>Active Services</h3>
          <p className="value">{data?.servicesCount || 0}</p>
        </div>
        <div className="insight-card">
          <h3>Monthly Usage</h3>
          <p className="value">{data?.usageGB || 0} GB</p>
        </div>
        <div className="insight-card">
          <h3>LTV</h3>
          <p className="value">{data?.ltv || 0} YER</p>
        </div>
      </div>
      <div className="customer-history">
        <h3>Recent Activity</h3>
        <div className="timeline">
          {data?.recentActivity?.map((item: any, i: number) => (
            <div key={i} className="timeline-item">
              <span className="date">{item.date}</span>
              <span className="action">{item.action}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
