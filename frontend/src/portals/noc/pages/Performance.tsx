import { useQuery } from '@tanstack/react-query';
import { networkAPI } from '../services/api';

export default function Performance() {
  const { data: metrics } = useQuery({ queryKey: ['performance'], queryFn: () => networkAPI.getPerformance() });

  const mockData = [
    { metric: 'CPU Usage', value: 45, threshold: 80 },
    { metric: 'Memory', value: 62, threshold: 85 },
    { metric: 'Disk I/O', value: 28, threshold: 70 },
    { metric: 'Network Latency', value: 12, threshold: 50 },
    { metric: 'Packet Loss', value: 0.1, threshold: 1 },
    { metric: 'Availability', value: 99.98, threshold: 99.9 }
  ];

  return (
    <div className="performance-page">
      <h2>Network Performance</h2>
      <div className="metrics-grid">
        {mockData.map(m => (
          <div key={m.metric} className="metric-card">
            <h3>{m.metric}</h3>
            <div className="metric-bar">
              <div className="bar" style={{ width: `${m.value}%`, background: m.value > m.threshold ? '#e74c3c' : '#27ae60' }}></div>
            </div>
            <p className="value">{m.value}{m.metric === 'Availability' ? '%' : '%'}</p>
            <span className="threshold">Threshold: {m.threshold}{m.metric === 'Availability' ? '%' : '%'}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
