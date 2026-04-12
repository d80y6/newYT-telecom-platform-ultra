import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

export default function ResourceInventory() {
  const { data, isLoading } = useQuery({ queryKey: ['resources'], queryFn: () => client.get('/resourceInventoryManagement/v4/resource').then(r => r.data) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="resource-inventory-page">
      <h2>Resource Inventory</h2>
      <div className="resource-stats">
        <div className="stat-card"><h3>Total Resources</h3><p className="value">{data?.length || 0}</p></div>
        <div className="stat-card"><h3>Active</h3><p className="value good">{data?.filter((r: any) => r.status === 'ACTIVE').length || 0}</p></div>
        <div className="stat-card"><h3>Maintenance</h3><p className="value warning">{data?.filter((r: any) => r.status === 'MAINTENANCE').length || 0}</p></div>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>Name</th><th>Type</th><th>Status</th><th>Location</th><th>Created</th></tr></thead>
        <tbody>
          {data?.map((item: any) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.name}</td>
              <td>{item.resourceType}</td>
              <td><span className={`status ${item.status?.toLowerCase()}`}>{item.status}</span></td>
              <td>{item.location}</td>
              <td>{item.createdAt}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
