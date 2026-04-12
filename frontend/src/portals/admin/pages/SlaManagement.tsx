import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

const slaAPI = {
  getAll: () => client.get('/slaManagement/v5/sla').then(r => r.data),
  getById: (id: string) => client.get(`/slaManagement/v5/sla/${id}`).then(r => r.data),
  create: (data: any) => client.post('/slaManagement/v5/sla', data).then(r => r.data)
};

export default function SlaManagement() {
  const { data: slas, isLoading } = useQuery({ queryKey: ['slas'], queryFn: slaAPI.getAll });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="sla-page">
      <h2>SLA Management</h2>
      <button className="primary" onClick={() => slaAPI.create({ name: 'New SLA', serviceLevel: '99.9' })}>Create SLA</button>
      <div className="sla-overview">
        <div className="sla-summary">
          <div className="summary-item">
            <h3>Total SLAs</h3>
            <p className="value">{slas?.length || 0}</p>
          </div>
          <div className="summary-item">
            <h3>Compliant</h3>
            <p className="value good">{slas?.filter((s: any) => s.status === 'COMPLIANT').length || 0}</p>
          </div>
          <div className="summary-item">
            <h3>Breached</h3>
            <p className="value bad">{slas?.filter((s: any) => s.status === 'BREACHED').length || 0}</p>
          </div>
        </div>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>Name</th><th>Service Level</th><th>Status</th><th>Customer</th><th>Valid Until</th></tr></thead>
        <tbody>
          {slas?.map((sla: any) => (
            <tr key={sla.id}>
              <td>{sla.id}</td>
              <td>{sla.name}</td>
              <td>{sla.serviceLevel}%</td>
              <td><span className={`status ${sla.status?.toLowerCase()}`}>{sla.status}</span></td>
              <td>{sla.customerName}</td>
              <td>{sla.validUntil}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
