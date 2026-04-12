import { useQuery } from '@tanstack/react-query';
import { networkAPI } from '../services/api';

export default function SlaMonitoring() {
  const { data: services, isLoading } = useQuery({ queryKey: ['services'], queryFn: () => networkAPI.getElements() });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="sla-monitoring-page">
      <h2>SLA Monitoring</h2>
      <div className="sla-overview">
        <div className="sla-card">
          <h3>Network Availability</h3>
          <p className="value">99.95%</p>
          <p className="target">Target: 99.99%</p>
          <div className="progress-bar"><div className="progress" style={{ width: '99.95%' }}></div></div>
        </div>
        <div className="sla-card">
          <h3>Average Response Time</h3>
          <p className="value">45ms</p>
          <p className="target">Target: 50ms</p>
          <div className="progress-bar"><div className="progress good" style={{ width: '90%' }}></div></div>
        </div>
        <div className="sla-card">
          <h3>Ticket Resolution</h3>
          <p className="value">4.2h</p>
          <p className="target">Target: 4h</p>
          <div className="progress-bar"><div className="progress warning" style={{ width: '85%' }}></div></div>
        </div>
      </div>
      <div className="sla-details">
        <h3>Service Level Agreements</h3>
        <table className="data-table">
          <thead>
            <tr>
              <th>Service</th>
              <th>Availability</th>
              <th>Response Time</th>
              <th>Resolution</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {services?.slice(0, 10).map((service: any) => (
              <tr key={service.id}>
                <td>{service.name}</td>
                <td>99.95%</td>
                <td>45ms</td>
                <td>4.2h</td>
                <td><span className="status good">Compliant</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
