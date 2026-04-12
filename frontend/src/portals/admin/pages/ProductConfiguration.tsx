import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

export default function ProductConfiguration() {
  const { data, isLoading } = useQuery({ queryKey: ['productConfig'], queryFn: () => client.get('/productConfiguration/v5/configuration').then(r => r.data) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="product-config-page">
      <h2>Product Configuration</h2>
      <button className="primary">New Configuration</button>
      <div className="config-grid">
        {data?.map((config: any) => (
          <div key={config.id} className="config-card">
            <div className="config-header">
              <h3>{config.name}</h3>
              <span className={`status ${config.status?.toLowerCase()}`}>{config.status}</span>
            </div>
            <div className="config-body">
              <p><strong>Product:</strong> {config.productName}</p>
              <p><strong>Type:</strong> {config.configType}</p>
              <div className="params">
                <strong>Parameters:</strong>
                <ul>
                  {Object.entries(config.parameters || {}).map(([k, v]) => (
                    <li key={k}>{k}: {String(v)}</li>
                  ))}
                </ul>
              </div>
            </div>
            <div className="config-actions">
              <button>Edit</button>
              <button>Clone</button>
              <button>Activate</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
