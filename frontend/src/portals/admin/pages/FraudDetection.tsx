import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

export default function FraudDetection() {
  const { data, isLoading } = useQuery({ queryKey: ['fraud'], queryFn: () => client.get('/fraudDetection/v5/alert').then(r => r.data) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="fraud-page">
      <h2>Fraud Detection</h2>
      <div className="fraud-overview">
        <div className="alert-card critical"><h3>Critical</h3><p className="value">{data?.filter((a: any) => a.severity === 'CRITICAL').length || 0}</p></div>
        <div className="alert-card high"><h3>High</h3><p className="value">{data?.filter((a: any) => a.severity === 'HIGH').length || 0}</p></div>
        <div className="alert-card medium"><h3>Medium</h3><p className="value">{data?.filter((a: any) => a.severity === 'MEDIUM').length || 0}</p></div>
        <div className="alert-card low"><h3>Low</h3><p className="value">{data?.filter((a: any) => a.severity === 'LOW').length || 0}</p></div>
      </div>
      <div className="fraud-list">
        {data?.map((alert: any) => (
          <div key={alert.id} className={`fraud-alert ${alert.severity?.toLowerCase()}`}>
            <div className="alert-header">
              <span className={`severity ${alert.severity?.toLowerCase()}`}>{alert.severity}</span>
              <span className="type">{alert.fraudType}</span>
              <span className="time">{alert.detectedAt}</span>
            </div>
            <div className="alert-body">
              <p>{alert.description}</p>
              <p className="account">Account: {alert.accountId}</p>
            </div>
            <div className="alert-actions">
              <button>Investigate</button>
              <button>Block</button>
              <button>Dismiss</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
