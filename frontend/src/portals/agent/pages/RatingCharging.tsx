import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

export default function RatingCharging() {
  const { data, isLoading } = useQuery({ queryKey: ['ratings'], queryFn: () => client.get('/rating/v5/rating').then(r => r.data) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="rating-charging-page">
      <h2>Rating & Charging</h2>
      <div className="charging-stats">
        <div className="stat"><h3>CDR Processed</h3><p className="value">1.2M</p></div>
        <div className="stat"><h3>Rating Time (Avg)</h3><p className="value">12ms</p></div>
        <div className="stat"><h3>Success Rate</h3><p className="value good">99.9%</p></div>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>CDR ID</th><th>Subscription</th><th>Usage</th><th>Rating</th><th>Charged</th></tr></thead>
        <tbody>
          {data?.map((item: any) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.cdrId}</td>
              <td>{item.subscriptionId}</td>
              <td>{item.usageAmount} {item.usageUnit}</td>
              <td>{item.ratingResult} YER</td>
              <td><span className="status charged">Charged</span></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
